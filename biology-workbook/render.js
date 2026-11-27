// Render build/*.html to samples/*.pdf (print-ready) and samples/*-p<N>.png (previews).
// Usage: node render.js
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const SIZES = { Letter: { w: 8.5, h: 11 }, A4: { w: 8.27, h: 11.69 } };

(async () => {
  const buildDir = path.join(__dirname, 'build');
  const outDir = path.join(__dirname, 'samples');
  fs.mkdirSync(outDir, { recursive: true });
  const browser = await chromium.launch();

  for (const file of fs.readdirSync(buildDir).filter(f => f.endsWith('.html'))) {
    const html = fs.readFileSync(path.join(buildDir, file), 'utf8');
    const size = SIZES[/size:(\w+)/.exec(html)[1]];
    const stem = file.replace(/\.html$/, '');
    const page = await browser.newPage({
      viewport: { width: Math.round(size.w * 96), height: Math.round(size.h * 96) },
      deviceScaleFactor: 2,
    });
    await page.goto('file://' + path.join(buildDir, file));
    await page.evaluate(() => document.fonts.ready);

    await page.pdf({ path: path.join(outDir, stem + '.pdf'), preferCSSPageSize: true, printBackground: true });

    const sections = await page.$$('section.page');
    for (let i = 0; i < sections.length; i++) {
      await sections[i].screenshot({ path: path.join(outDir, `${stem}-p${i + 1}.png`) });
    }
    console.log('rendered', stem, `(${sections.length} pages)`);
    await page.close();
  }
  await browser.close();
})();
