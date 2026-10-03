import { defineConfig, devices } from '@playwright/test';

// chromium: execução headless · demo: navegador visível em câmera lenta · firefox/webkit/mobile: outros alvos
export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  retries: 0,
  reporter: [['list'], ['html', { open: 'never' }]],

  snapshotPathTemplate: '{testDir}/__screenshots__/{testFileName}/{arg}{ext}',

  use: {
    baseURL: 'http://localhost:3000',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },

  webServer: {
    command: 'node server.js',
    url: 'http://localhost:3000',
    reuseExistingServer: true,
    timeout: 30_000,
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'demo',
      use: {
        ...devices['Desktop Chrome'],
        headless: false,
        viewport: { width: 1280, height: 720 },
        launchOptions: { slowMo: 600 },
      },
      fullyParallel: false,
    },
    // Exigem `npx playwright install firefox webkit`. Testes visuais rodam só no Chromium.
    { name: 'firefox', use: { ...devices['Desktop Firefox'] }, grepInvert: /@visual/ },
    { name: 'webkit', use: { ...devices['Desktop Safari'] }, grepInvert: /@visual/ },
    { name: 'mobile', use: { ...devices['Pixel 7'] }, grepInvert: /@visual/ },
  ],
});
