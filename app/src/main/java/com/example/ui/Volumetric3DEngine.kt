package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Hotspot3DPart
import com.example.data.Interactive4KModel
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.TealPrimary
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

data class Vec3(val x: Float, val y: Float, val z: Float)

data class ProjectedPoint(
    val screenX: Float,
    val screenY: Float,
    val depthZ: Float,
    val scale: Float
)

data class Face3D(
    val vertices: List<Vec3>,
    val baseColor: Color,
    val borderColor: Color? = null,
    val alpha: Float = 1f,
    val isWireframe: Boolean = false
)

enum class RenderLayerMode {
    HYBRID_3D_AND_4K,
    PURE_VOLUMETRIC_3D,
    HOLOGRAPHIC_4K
}

/**
 * Pure mathematical 3D transformation with full 360° Yaw, 360° Pitch, and 360° Roll Euler matrices
 * and perspective projection.
 */
object Engine3DMath {
    fun rotate360(
        point: Vec3,
        yawDeg: Float,
        pitchDeg: Float,
        rollDeg: Float = 0f
    ): Vec3 {
        val yaw = (yawDeg * PI / 180.0).toFloat()
        val pitch = (pitchDeg * PI / 180.0).toFloat()
        val roll = (rollDeg * PI / 180.0).toFloat()

        // 1. Rotate around Y axis (Yaw 0..360)
        val cosY = cos(yaw)
        val sinY = sin(yaw)
        val x1 = point.x * cosY + point.z * sinY
        val z1 = -point.x * sinY + point.z * cosY
        val y1 = point.y

        // 2. Rotate around X axis (Pitch 0..360)
        val cosP = cos(pitch)
        val sinP = sin(pitch)
        val y2 = y1 * cosP - z1 * sinP
        val z2 = y1 * sinP + z1 * cosP
        val x2 = x1

        // 3. Rotate around Z axis (Roll 0..360)
        val cosR = cos(roll)
        val sinR = sin(roll)
        val x3 = x2 * cosR - y2 * sinR
        val y3 = x2 * sinR + y2 * cosR

        return Vec3(x3, y3, z2)
    }

    fun project(
        worldPoint: Vec3,
        yawDeg: Float,
        pitchDeg: Float,
        rollDeg: Float,
        centerX: Float,
        centerY: Float,
        baseRadius: Float,
        zoomScale: Float
    ): ProjectedPoint {
        val rotated = rotate360(worldPoint, yawDeg, pitchDeg, rollDeg)
        val cameraDist = 3.8f
        val perspective = cameraDist / (cameraDist - rotated.z).coerceAtLeast(0.8f)
        val finalScale = perspective * zoomScale
        val sx = centerX + rotated.x * baseRadius * finalScale
        val sy = centerY + rotated.y * baseRadius * finalScale
        return ProjectedPoint(sx, sy, rotated.z, finalScale)
    }

    /**
     * Maps each Hotspot3DPart to its true 3D volumetric coordinates (x, y, z in -1.2f..1.2f)
     */
    fun getPart3DCoords(part: Hotspot3DPart, explodeFactor: Float): Vec3 {
        val ex = 1f + explodeFactor * 0.45f
        val baseVec = when (part.partId) {
            // Biology: Mitochondria & Plant Cell 3D coordinates
            "part_cristae" -> Vec3(0.42f, -0.05f, 0.32f)
            "part_outer_membrane" -> Vec3(0.75f, -0.35f, -0.25f)
            "part_matrix_enzymes" -> Vec3(0.35f, 0.38f, 0.42f)
            "part_chloroplast_grana" -> Vec3(-0.55f, 0.45f, 0.25f)
            "part_nucleus_nucleolus" -> Vec3(-0.48f, -0.32f, -0.30f)

            // Physics: Parallel Plate Capacitor & Dielectric 3D coordinates
            "part_plates" -> Vec3(-0.15f, -0.52f, 0.30f)
            "part_dielectric_slab" -> Vec3(0.22f, -0.02f, 0.45f)
            "part_electric_field" -> Vec3(-0.38f, 0.08f, -0.25f)
            "part_voltmeter" -> Vec3(0.65f, 0.58f, 0.35f)

            // Chemistry: Daniell Galvanic Cell 3D coordinates
            "part_zinc_anode" -> Vec3(-0.58f, 0.15f, 0.32f)
            "part_copper_cathode" -> Vec3(0.58f, 0.15f, 0.32f)
            "part_salt_bridge" -> Vec3(0.0f, -0.28f, 0.40f)
            "part_external_wire" -> Vec3(0.0f, -0.78f, -0.15f)

            else -> Vec3(
                (part.normalizedX - 0.5f) * 1.6f,
                (part.normalizedY - 0.5f) * 1.6f,
                part.normalizedZ * 1.2f
            )
        }
        return Vec3(baseVec.x * ex, baseVec.y * ex, baseVec.z * ex)
    }
}

@Composable
fun Volumetric3DSceneCanvas(
    model: Interactive4KModel,
    selectedPart: Hotspot3DPart,
    yawAngleDeg: Float,
    pitchAngleDeg: Float,
    rollAngleDeg: Float,
    zoomScale: Float,
    explodeLayersFactor: Float,
    simulationIntensity: Float,
    animPhase: Float,
    showVolumetricMesh: Boolean,
    onOrbitDrag: (Float, Float) -> Unit,
    onHotspotTapped: (Hotspot3DPart) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    val meshFaces = remember(model.modelId, explodeLayersFactor, simulationIntensity) {
        when (model.modelId) {
            "model_bio_mitochondria" -> buildMitochondriaAndCell3DMesh(explodeLayersFactor)
            "model_phys_capacitor" -> buildCapacitor3DMesh(explodeLayersFactor, simulationIntensity)
            "model_chem_daniell" -> buildDaniellCell3DMesh(explodeLayersFactor)
            else -> buildMitochondriaAndCell3DMesh(explodeLayersFactor)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(model.modelId) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onOrbitDrag(dragAmount.x, dragAmount.y)
                }
            }
            .pointerInput(
                model.modelId,
                yawAngleDeg,
                pitchAngleDeg,
                rollAngleDeg,
                zoomScale,
                explodeLayersFactor
            ) {
                detectTapGestures { tapOffset ->
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val radius = minOf(size.width, size.height) * 0.36f

                    var closestPart: Hotspot3DPart? = null
                    var minDist = Float.MAX_VALUE

                    model.parts.forEach { part ->
                        val vec3 = Engine3DMath.getPart3DCoords(part, explodeLayersFactor)
                        val proj = Engine3DMath.project(
                            worldPoint = vec3,
                            yawDeg = yawAngleDeg,
                            pitchDeg = pitchAngleDeg,
                            rollDeg = rollAngleDeg,
                            centerX = cx,
                            centerY = cy,
                            baseRadius = radius,
                            zoomScale = zoomScale
                        )
                        val dist = hypot(tapOffset.x - proj.screenX, tapOffset.y - proj.screenY)
                        if (dist < minDist) {
                            minDist = dist
                            closestPart = part
                        }
                    }

                    if (closestPart != null && minDist <= 130f) {
                        onHotspotTapped(closestPart!!)
                    }
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val baseRadius = minOf(w, h) * 0.36f

        // 1. Draw 360° 3D Orbital Compass & Equatorial / Meridian Rings
        draw360OrbitalGyroscope(
            cx = cx,
            cy = cy,
            baseRadius = baseRadius,
            yawDeg = yawAngleDeg,
            pitchDeg = pitchAngleDeg,
            rollDeg = rollAngleDeg,
            zoomScale = zoomScale
        )

        // 2. Render Depth-Sorted Volumetric 3D Faces (Painter's Algorithm with Directional Lighting)
        if (showVolumetricMesh) {
            val lightDir = Vec3(0.45f, -0.65f, 0.62f)
            val projectedFaces = meshFaces.map { face ->
                val rotatedVerts = face.vertices.map {
                    Engine3DMath.rotate360(it, yawAngleDeg, pitchAngleDeg, rollAngleDeg)
                }
                val avgZ = rotatedVerts.map { it.z }.average().toFloat()

                // Compute face normal for realistic 3D shading
                val shadeFactor = if (rotatedVerts.size >= 3) {
                    val u = Vec3(
                        rotatedVerts[1].x - rotatedVerts[0].x,
                        rotatedVerts[1].y - rotatedVerts[0].y,
                        rotatedVerts[1].z - rotatedVerts[0].z
                    )
                    val v = Vec3(
                        rotatedVerts[2].x - rotatedVerts[0].x,
                        rotatedVerts[2].y - rotatedVerts[0].y,
                        rotatedVerts[2].z - rotatedVerts[0].z
                    )
                    val nx = u.y * v.z - u.z * v.y
                    val ny = u.z * v.x - u.x * v.z
                    val nz = u.x * v.y - u.y * v.x
                    val len = max(0.0001f, kotlin.math.sqrt(nx * nx + ny * ny + nz * nz))
                    val dot = abs((nx / len) * lightDir.x + (ny / len) * lightDir.y + (nz / len) * lightDir.z)
                    (0.58f + 0.42f * dot).coerceIn(0.45f, 1.15f)
                } else {
                    0.85f
                }

                val screenPts = face.vertices.map {
                    Engine3DMath.project(
                        it,
                        yawAngleDeg,
                        pitchAngleDeg,
                        rollAngleDeg,
                        cx,
                        cy,
                        baseRadius,
                        zoomScale
                    )
                }
                Triple(face, screenPts, avgZ to shadeFactor)
            }.sortedBy { it.third.first } // Back-to-front depth sort

            projectedFaces.forEach { (face, pts, depthAndShade) ->
                val shade = depthAndShade.second
                val path = Path().apply {
                    pts.forEachIndexed { idx, pt ->
                        if (idx == 0) moveTo(pt.screenX, pt.screenY)
                        else lineTo(pt.screenX, pt.screenY)
                    }
                    close()
                }

                val shadedColor = Color(
                    red = (face.baseColor.red * shade).coerceIn(0f, 1f),
                    green = (face.baseColor.green * shade).coerceIn(0f, 1f),
                    blue = (face.baseColor.blue * shade).coerceIn(0f, 1f),
                    alpha = face.alpha
                )

                if (!face.isWireframe) {
                    drawPath(path = path, color = shadedColor, style = Fill)
                }

                val strokeCol = face.borderColor ?: shadedColor.copy(alpha = (face.alpha + 0.2f).coerceAtMost(1f))
                drawPath(
                    path = path,
                    color = strokeCol,
                    style = Stroke(width = if (face.isWireframe) 2.2.dp.toPx() else 1.2.dp.toPx())
                )
            }
        }

        // 3. Draw 3D Animated Scientific Particles (ATP / Electric Field / Electrons & Ions) in 3D space
        draw3DScientificParticles(
            modelId = model.modelId,
            yawDeg = yawAngleDeg,
            pitchDeg = pitchAngleDeg,
            rollDeg = rollAngleDeg,
            cx = cx,
            cy = cy,
            baseRadius = baseRadius,
            zoomScale = zoomScale,
            simulationIntensity = simulationIntensity,
            animPhase = animPhase
        )

        // 4. Draw 3D Depth-Sorted Interactive Hotspots & Arabic Callout Badges
        val sortedParts = model.parts.map { part ->
            val vec3 = Engine3DMath.getPart3DCoords(part, explodeLayersFactor)
            val proj = Engine3DMath.project(
                vec3,
                yawAngleDeg,
                pitchAngleDeg,
                rollAngleDeg,
                cx,
                cy,
                baseRadius,
                zoomScale
            )
            val anchorProj = Engine3DMath.project(
                Vec3(vec3.x * 0.55f, vec3.y * 0.55f, vec3.z * 0.55f),
                yawAngleDeg,
                pitchAngleDeg,
                rollAngleDeg,
                cx,
                cy,
                baseRadius,
                zoomScale
            )
            Triple(part, proj, anchorProj)
        }.sortedBy { it.second.depthZ }

        sortedParts.forEach { (part, proj, anchorProj) ->
            val isSelected = part.partId == selectedPart.partId
            val depthScale = proj.scale.coerceIn(0.65f, 1.45f)

            // 3D Leader Line from internal organ/component anchor to floating 3D pin
            drawLine(
                color = if (isSelected) Color(0xFFFACC15) else Color.White.copy(alpha = 0.85f),
                start = Offset(anchorProj.screenX, anchorProj.screenY),
                end = Offset(proj.screenX, proj.screenY),
                strokeWidth = (if (isSelected) 3.dp.toPx() else 1.8.dp.toPx()) * depthScale
            )

            // Small anchor dot inside model
            drawCircle(
                color = Color(0xFFFACC15),
                radius = 4.dp.toPx() * depthScale,
                center = Offset(anchorProj.screenX, anchorProj.screenY)
            )

            // Outer glowing 3D sphere halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = if (isSelected) {
                        listOf(CoralSecondary.copy(alpha = 0.75f), Color.Transparent)
                    } else {
                        listOf(TealPrimary.copy(alpha = 0.65f), Color.Transparent)
                    },
                    center = Offset(proj.screenX, proj.screenY),
                    radius = (if (isSelected) 28.dp.toPx() else 20.dp.toPx()) * depthScale
                ),
                radius = (if (isSelected) 28.dp.toPx() else 20.dp.toPx()) * depthScale,
                center = Offset(proj.screenX, proj.screenY)
            )

            // Solid 3D Pin Sphere
            drawCircle(
                color = if (isSelected) CoralSecondary else TealPrimary,
                radius = (if (isSelected) 12.dp.toPx() else 9.5.dp.toPx()) * depthScale,
                center = Offset(proj.screenX, proj.screenY)
            )

            // Specular highlight on 3D Pin Sphere
            drawCircle(
                color = Color.White,
                radius = 3.8.dp.toPx() * depthScale,
                center = Offset(
                    proj.screenX - 2.5.dp.toPx() * depthScale,
                    proj.screenY - 2.5.dp.toPx() * depthScale
                )
            )

            // Draw Floating 3D Label Pill for Selected Part (or front-facing parts)
            if (isSelected || proj.depthZ > -0.15f) {
                draw3DPartLabel(
                    textMeasurer = textMeasurer,
                    label = part.nameAr.substringBefore(" ("),
                    x = proj.screenX,
                    y = proj.screenY - (18.dp.toPx() * depthScale),
                    isSelected = isSelected
                )
            }
        }
    }
}

private fun DrawScope.draw3DPartLabel(
    textMeasurer: TextMeasurer,
    label: String,
    x: Float,
    y: Float,
    isSelected: Boolean
) {
    val textLayout = textMeasurer.measure(
        text = label,
        style = TextStyle(
            color = Color.White,
            fontSize = if (isSelected) 12.sp else 10.sp,
            fontWeight = FontWeight.Bold
        )
    )
    val padX = 8.dp.toPx()
    val padY = 4.dp.toPx()
    val boxW = textLayout.size.width + padX * 2
    val boxH = textLayout.size.height + padY * 2
    val left = (x - boxW / 2f).coerceIn(8f, size.width - boxW - 8f)
    val top = (y - boxH).coerceIn(8f, size.height - boxH - 8f)

    drawRoundRect(
        color = if (isSelected) CoralSecondary.copy(alpha = 0.92f) else Color(0xCC0F172A),
        topLeft = Offset(left, top),
        size = androidx.compose.ui.geometry.Size(boxW, boxH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
    )
    drawRoundRect(
        color = if (isSelected) Color(0xFFFACC15) else Color.White.copy(alpha = 0.45f),
        topLeft = Offset(left, top),
        size = androidx.compose.ui.geometry.Size(boxW, boxH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )
    drawText(
        textLayoutResult = textLayout,
        topLeft = Offset(left + padX, top + padY)
    )
}

private fun DrawScope.draw360OrbitalGyroscope(
    cx: Float,
    cy: Float,
    baseRadius: Float,
    yawDeg: Float,
    pitchDeg: Float,
    rollDeg: Float,
    zoomScale: Float
) {
    val segments = 36
    val ringRadius = 1.18f

    // Equatorial XZ 360° Ring
    val xzPath = Path()
    for (i in 0..segments) {
        val angle = (i.toFloat() / segments) * 2f * PI.toFloat()
        val pt = Vec3(cos(angle) * ringRadius, 0.72f, sin(angle) * ringRadius)
        val proj = Engine3DMath.project(pt, yawDeg, pitchDeg, rollDeg, cx, cy, baseRadius, zoomScale)
        if (i == 0) xzPath.moveTo(proj.screenX, proj.screenY)
        else xzPath.lineTo(proj.screenX, proj.screenY)
    }
    drawPath(
        path = xzPath,
        color = Color(0xFF38BDF8).copy(alpha = 0.30f),
        style = Stroke(
            width = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
        )
    )

    // Meridian XY 360° Ring
    val xyPath = Path()
    for (i in 0..segments) {
        val angle = (i.toFloat() / segments) * 2f * PI.toFloat()
        val pt = Vec3(cos(angle) * ringRadius, sin(angle) * ringRadius, 0f)
        val proj = Engine3DMath.project(pt, yawDeg, pitchDeg, rollDeg, cx, cy, baseRadius, zoomScale)
        if (i == 0) xyPath.moveTo(proj.screenX, proj.screenY)
        else xyPath.lineTo(proj.screenX, proj.screenY)
    }
    drawPath(
        path = xyPath,
        color = Color.White.copy(alpha = 0.14f),
        style = Stroke(width = 1.dp.toPx())
    )
}

private fun DrawScope.draw3DScientificParticles(
    modelId: String,
    yawDeg: Float,
    pitchDeg: Float,
    rollDeg: Float,
    cx: Float,
    cy: Float,
    baseRadius: Float,
    zoomScale: Float,
    simulationIntensity: Float,
    animPhase: Float
) {
    val count = (simulationIntensity * 6).toInt().coerceIn(6, 36)
    for (i in 0 until count) {
        val t = (animPhase + i.toFloat() / count) % 1f
        val worldPt = when (modelId) {
            "model_phys_capacitor" -> {
                // Electric field particles flowing from top plate (+Q) to bottom plate (-Q) in 3D grid
                val col = (i % 6) - 2.5f
                val row = ((i / 6) % 4) - 1.5f
                Vec3(
                    x = col * 0.25f,
                    y = -0.45f + t * 0.90f,
                    z = row * 0.22f
                )
            }
            "model_chem_daniell" -> {
                // Electrons flowing along upper 3D wire from Anode (-X) to Cathode (+X)
                val arcX = -0.60f + t * 1.20f
                val arcY = -0.68f - sin(t * PI.toFloat()) * 0.22f
                Vec3(x = arcX, y = arcY, z = 0.05f)
            }
            else -> {
                // Glowing ATP energy molecules spiraling inside the 3D Mitochondria matrix
                val angle = t * 4f * PI.toFloat() + i
                Vec3(
                    x = 0.42f + cos(angle) * 0.22f,
                    y = -0.35f + t * 0.70f,
                    z = sin(angle) * 0.22f
                )
            }
        }

        val proj = Engine3DMath.project(
            worldPt,
            yawDeg,
            pitchDeg,
            rollDeg,
            cx,
            cy,
            baseRadius,
            zoomScale
        )

        drawCircle(
            color = Color(0xFFFACC15).copy(alpha = 0.9f),
            radius = 5.dp.toPx() * proj.scale.coerceIn(0.6f, 1.4f),
            center = Offset(proj.screenX, proj.screenY)
        )
    }
}

/**
 * Builds a true 3D multi-layered mesh for Biology:
 * - Right side: 3D Cutaway Mitochondrion with Outer Membrane shell, folded Cristae (الأعراف) ribbons, and Matrix.
 * - Left side: 3D Plant Cell hexagonal wall, Chloroplast Grana stacks, and 3D Nucleus/Nucleolus sphere.
 */
private fun buildMitochondriaAndCell3DMesh(explode: Float): List<Face3D> {
    val faces = mutableListOf<Face3D>()
    val shiftRight = 0.42f + explode * 0.18f
    val shiftLeft = -0.45f - explode * 0.18f

    // 1. Mitochondrion Outer Capsule Shell (Back half + Front cutaway)
    val latSteps = 8
    val lonSteps = 10
    for (i in 0 until latSteps) {
        val theta1 = -PI.toFloat() / 2f + (i.toFloat() / latSteps) * PI.toFloat()
        val theta2 = -PI.toFloat() / 2f + ((i + 1).toFloat() / latSteps) * PI.toFloat()
        for (j in 0 until lonSteps) {
            // Leave front window open for cutaway dissection
            val phi1 = (j.toFloat() / lonSteps) * 1.45f * PI.toFloat() + 0.4f
            val phi2 = ((j + 1).toFloat() / lonSteps) * 1.45f * PI.toFloat() + 0.4f

            fun capsulePt(theta: Float, phi: Float): Vec3 {
                val rx = 0.36f
                val ry = 0.62f
                val rz = 0.34f
                return Vec3(
                    x = shiftRight + rx * cos(theta) * cos(phi),
                    y = ry * sin(theta),
                    z = rz * cos(theta) * sin(phi)
                )
            }

            faces.add(
                Face3D(
                    vertices = listOf(
                        capsulePt(theta1, phi1),
                        capsulePt(theta1, phi2),
                        capsulePt(theta2, phi2),
                        capsulePt(theta2, phi1)
                    ),
                    baseColor = Color(0xFFEA580C),
                    alpha = 0.88f
                )
            )
        }
    }

    // 2. Mitochondrion Inner Folded Cristae (الأعراف) 3D Shelves
    val cristaYLevels = listOf(-0.36f, -0.18f, 0.0f, 0.18f, 0.36f)
    cristaYLevels.forEachIndexed { idx, yLevel ->
        val zFront = 0.22f + explode * 0.12f
        val zBack = -0.20f
        val xLeft = shiftRight - 0.26f
        val xRight = shiftRight + 0.24f
        faces.add(
            Face3D(
                vertices = listOf(
                    Vec3(xLeft, yLevel - 0.04f, zBack),
                    Vec3(xRight, yLevel - 0.04f, zBack),
                    Vec3(xRight, yLevel + 0.04f, zFront),
                    Vec3(xLeft, yLevel + 0.04f, zFront)
                ),
                baseColor = if (idx % 2 == 0) Color(0xFFF43F5E) else Color(0xFFFB7185),
                borderColor = Color(0xFFFDE047),
                alpha = 0.92f
            )
        )
    }

    // 3. Plant Cell Wall 3D Hexagonal Prism Frame (Left side)
    val cellWallVertsTop = listOf(
        Vec3(shiftLeft - 0.35f, -0.58f, -0.28f),
        Vec3(shiftLeft + 0.25f, -0.58f, -0.28f),
        Vec3(shiftLeft + 0.25f, -0.58f, 0.28f),
        Vec3(shiftLeft - 0.35f, -0.58f, 0.28f)
    )
    val cellWallVertsBottom = cellWallVertsTop.map { Vec3(it.x, 0.58f, it.z) }

    // Back & Side Cell Walls (Cellulose + Lignin Emerald Green)
    faces.add(
        Face3D(
            vertices = listOf(
                cellWallVertsTop[0],
                cellWallVertsTop[1],
                cellWallVertsBottom[1],
                cellWallVertsBottom[0]
            ),
            baseColor = Color(0xFF0D9488),
            borderColor = Color(0xFF5EEAD4),
            alpha = 0.85f
        )
    )
    faces.add(
        Face3D(
            vertices = listOf(
                cellWallVertsTop[0],
                cellWallVertsTop[3],
                cellWallVertsBottom[3],
                cellWallVertsBottom[0]
            ),
            baseColor = Color(0xFF14B8A6),
            borderColor = Color(0xFF5EEAD4),
            alpha = 0.82f
        )
    )

    // 4. Nucleus & Nucleolus 3D Octahedron/Sphere inside Plant Cell
    val nucCenter = Vec3(shiftLeft - 0.04f, -0.22f, 0.05f + explode * 0.1f)
    faces.addAll(build3DBox(nucCenter, 0.18f, 0.18f, 0.18f, Color(0xFF0284C7), 0.90f))

    // 5. Chloroplast Grana Disks (Bottom of Plant Cell)
    val granaCenter = Vec3(shiftLeft - 0.06f, 0.34f, 0.10f + explode * 0.12f)
    faces.addAll(build3DBox(granaCenter, 0.20f, 0.12f, 0.16f, Color(0xFF16A34A), 0.92f))

    return faces
}

/**
 * Builds a true 3D Parallel-Plate Capacitor with:
 * - Upper Golden Conductive Plate (+Q)
 * - Lower Golden Conductive Plate (-Q)
 * - Translucent Blue Dielectric Slab (العازل الكهربائي k) sliding in 3D space between the plates
 * - Digital Voltmeter 3D Box
 */
private fun buildCapacitor3DMesh(explode: Float, kValue: Float): List<Face3D> {
    val faces = mutableListOf<Face3D>()
    val plateSeparation = 0.42f + explode * 0.25f

    // 1. Upper Conductive Plate (+Q)
    faces.addAll(
        build3DBox(
            center = Vec3(0f, -plateSeparation, 0f),
            halfW = 0.72f,
            halfH = 0.06f,
            halfD = 0.52f,
            color = Color(0xFFEAB308),
            alpha = 0.92f
        )
    )

    // 2. Lower Conductive Plate (-Q)
    faces.addAll(
        build3DBox(
            center = Vec3(0f, plateSeparation, 0f),
            halfW = 0.72f,
            halfH = 0.06f,
            halfD = 0.52f,
            color = Color(0xFFCA8A04),
            alpha = 0.92f
        )
    )

    // 3. Dielectric Slab (العازل الكهربائي) whose insertion depth responds to simulationIntensity (k)
    val insertionOffset = (1f - (kValue - 1f) / 5f) * 0.45f
    faces.addAll(
        build3DBox(
            center = Vec3(insertionOffset, 0f, 0.05f),
            halfW = 0.64f,
            halfH = 0.18f,
            halfD = 0.44f,
            color = Color(0xFF38BDF8),
            alpha = 0.68f
        )
    )

    // 4. Voltmeter 3D Module at bottom-right
    faces.addAll(
        build3DBox(
            center = Vec3(0.62f, 0.58f, 0.25f),
            halfW = 0.22f,
            halfH = 0.10f,
            halfD = 0.16f,
            color = Color(0xFF2563EB),
            alpha = 0.90f
        )
    )

    return faces
}

/**
 * Builds a true 3D Daniell Galvanic Cell (خلية دانيال الكلفانية):
 * - Left Beaker with ZnSO4 solution & metallic Zinc Anode bar
 * - Right Beaker with CuSO4 solution & copper Cathode bar
 * - U-shaped 3D Salt Bridge (الجسر الملحي) connecting the two beakers
 * - Upper Ammeter/Voltmeter Gauge
 */
private fun buildDaniellCell3DMesh(explode: Float): List<Face3D> {
    val faces = mutableListOf<Face3D>()
    val sepX = 0.55f + explode * 0.15f

    // 1. Left Electrolyte Beaker (ZnSO4)
    faces.addAll(
        build3DBox(
            center = Vec3(-sepX, 0.28f, 0f),
            halfW = 0.30f,
            halfH = 0.34f,
            halfD = 0.28f,
            color = Color(0xFF7DD3FC),
            alpha = 0.50f
        )
    )

    // 2. Zinc Anode 3D Electrode Plate (Zn)
    faces.addAll(
        build3DBox(
            center = Vec3(-sepX - 0.06f, 0.08f - explode * 0.12f, 0.04f),
            halfW = 0.09f,
            halfH = 0.42f,
            halfD = 0.04f,
            color = Color(0xFF94A3B8),
            alpha = 0.96f
        )
    )

    // 3. Right Electrolyte Beaker (CuSO4)
    faces.addAll(
        build3DBox(
            center = Vec3(sepX, 0.28f, 0f),
            halfW = 0.30f,
            halfH = 0.34f,
            halfD = 0.28f,
            color = Color(0xFF0284C7),
            alpha = 0.58f
        )
    )

    // 4. Copper Cathode 3D Electrode Plate (Cu)
    faces.addAll(
        build3DBox(
            center = Vec3(sepX + 0.06f, 0.08f - explode * 0.12f, 0.04f),
            halfW = 0.09f,
            halfH = 0.42f,
            halfD = 0.04f,
            color = Color(0xFFD97706),
            alpha = 0.96f
        )
    )

    // 5. U-Shaped Salt Bridge (الجسر الملحي)
    val bridgeY = -0.22f - explode * 0.15f
    // Horizontal top bar of U-bridge
    faces.addAll(
        build3DBox(
            center = Vec3(0f, bridgeY, 0.08f),
            halfW = sepX * 0.78f,
            halfH = 0.07f,
            halfD = 0.07f,
            color = Color(0xFFFDE68A),
            alpha = 0.85f
        )
    )
    // Left & Right vertical legs of U-bridge
    faces.addAll(
        build3DBox(
            center = Vec3(-sepX * 0.70f, bridgeY + 0.24f, 0.08f),
            halfW = 0.06f,
            halfH = 0.24f,
            halfD = 0.06f,
            color = Color(0xFFFDE68A),
            alpha = 0.82f
        )
    )
    faces.addAll(
        build3DBox(
            center = Vec3(sepX * 0.70f, bridgeY + 0.24f, 0.08f),
            halfW = 0.06f,
            halfH = 0.24f,
            halfD = 0.06f,
            color = Color(0xFFFDE68A),
            alpha = 0.82f
        )
    )

    // 6. Top External Meter Box
    faces.addAll(
        build3DBox(
            center = Vec3(0f, -0.72f, 0f),
            halfW = 0.18f,
            halfH = 0.14f,
            halfD = 0.08f,
            color = Color(0xFF1E293B),
            alpha = 0.95f
        )
    )

    return faces
}

private fun build3DBox(
    center: Vec3,
    halfW: Float,
    halfH: Float,
    halfD: Float,
    color: Color,
    alpha: Float
): List<Face3D> {
    val x0 = center.x - halfW
    val x1 = center.x + halfW
    val y0 = center.y - halfH
    val y1 = center.y + halfH
    val z0 = center.z - halfD
    val z1 = center.z + halfD

    val v000 = Vec3(x0, y0, z0)
    val v100 = Vec3(x1, y0, z0)
    val v110 = Vec3(x1, y1, z0)
    val v010 = Vec3(x0, y1, z0)

    val v001 = Vec3(x0, y0, z1)
    val v101 = Vec3(x1, y0, z1)
    val v111 = Vec3(x1, y1, z1)
    val v011 = Vec3(x0, y1, z1)

    return listOf(
        Face3D(listOf(v001, v101, v111, v011), color, alpha = alpha), // Front
        Face3D(listOf(v100, v000, v010, v110), color, alpha = alpha), // Back
        Face3D(listOf(v000, v100, v101, v001), color, alpha = alpha), // Top
        Face3D(listOf(v011, v111, v110, v010), color, alpha = alpha), // Bottom
        Face3D(listOf(v000, v001, v011, v010), color, alpha = alpha), // Left
        Face3D(listOf(v101, v100, v110, v111), color, alpha = alpha)  // Right
    )
}
