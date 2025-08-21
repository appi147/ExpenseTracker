import { describe, it, expect } from 'vitest'
import { cn } from '../../src/lib/utils'

describe('lib/utils cn', () => {
  it('merges classes and dedupes tailwind conflicts', () => {
    expect(cn('p-2', 'p-4')).toContain('p-4')
  })

  it('handles conditional and falsy inputs', () => {
    expect(cn('a', undefined, null, false && 'x', 0 && 'y', 'b')).toBe('a b')
  })
})
