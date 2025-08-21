import { describe, it, expect, beforeEach } from 'vitest'

import { setToken, getToken, clearToken } from '../../src/utils/auth'

describe('utils/auth', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('stores and reads token from localStorage', () => {
    expect(getToken()).toBeNull()
    setToken('abc123')
    expect(getToken()).toBe('abc123')
  })

  it('clears token from localStorage', () => {
    setToken('to-be-cleared')
    clearToken()
    expect(getToken()).toBeNull()
  })
})
