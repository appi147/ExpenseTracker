import '@testing-library/jest-dom'

// Polyfills for JSDOM
;(globalThis as any).ResizeObserver = (globalThis as any).ResizeObserver || class {
  observe() {}
  unobserve() {}
  disconnect() {}
}

// scrollIntoView polyfill for Radix Select
if (!(HTMLElement.prototype as any).scrollIntoView) {
  ;(HTMLElement.prototype as any).scrollIntoView = function () {}
}