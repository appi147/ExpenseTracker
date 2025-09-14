import '@testing-library/jest-dom'

(globalThis as any).ResizeObserver = (globalThis as any).ResizeObserver || class {
  // No-op implementation for JSDOM tests
  observe(): void {
    /* intentionally empty */
  }
  unobserve(): void {
    /* intentionally empty */
  }
  disconnect(): void {
    /* intentionally empty */
  }
}

if (!(HTMLElement.prototype as any).scrollIntoView) {
  (HTMLElement.prototype as any).scrollIntoView = function (): void {
    /* intentionally empty: jsdom doesn't support scrollIntoView */
  }
}
