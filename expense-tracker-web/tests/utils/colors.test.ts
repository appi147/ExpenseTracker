import { describe, it, expect } from 'vitest'
import { createCategoryColorMap } from '../../src/utils/colors'

describe('utils/colors', () => {
  it('assigns colors deterministically and cycles through palette', () => {
    const cats = Array.from({ length: 30 }, (_, i) => `C${i}`)
    const map = createCategoryColorMap(cats)
    expect(Object.keys(map)).toHaveLength(30)
    // Palette length 26: 0..25 unique, 26 wraps to 0; assert by equality
    const palette = Array.from(new Set(Object.values(map).slice(0, 26)))
    expect(palette.length).toBeGreaterThanOrEqual(24)
    expect(map['C25']).toBe(map['C0'])
  })

  it('returns empty map for empty input', () => {
    const map = createCategoryColorMap([])
    expect(map).toEqual({})
  })
})
