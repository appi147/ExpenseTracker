import { describe, it, expect, vi, beforeEach } from 'vitest'
import API, { Theme, getUserProfile, updateBudget, updateUserTheme } from '../../src/services/api'
import * as auth from '../../src/utils/auth'

vi.mock('axios', async (orig) => {
  const mod = await orig()
  const axios = mod.default
  const create = vi.fn(() => {
    const api: any = {
      interceptors: { request: { use: vi.fn((fn: any) => (api._handler = fn)) } },
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn(),
      _handler: undefined,
    }
    return api
  })
  return { default: { ...axios, create } }
})

describe('services/api', () => {
  beforeEach(() => {
    vi.resetModules()
    vi.spyOn(auth, 'getToken').mockReturnValue(null)
  })

  it('adds Authorization header when token exists', async () => {
    vi.spyOn(auth, 'getToken').mockReturnValue('t0k3n')
    const req = { headers: {} as Record<string, string> }
    // invoke stored interceptor handler
    await (API as any)._handler?.(req)
    expect(req.headers.Authorization).toBe('Bearer t0k3n')
  })

  it('does not add Authorization when no token', async () => {
    const req = { headers: {} as Record<string, string> }
    await (API as any)._handler?.(req)
    expect(req.headers.Authorization).toBeUndefined()
  })

  it('getUserProfile posts to /user/login', async () => {
    // @ts-expect-error mock shape
    API.post = vi.fn().mockResolvedValue({ data: { ok: true } })
    const res = await getUserProfile()
    expect(API.post).toHaveBeenCalledWith('/user/login')
    expect(res).toEqual({ ok: true })
  })

  it('updateBudget PUTs amount and returns data', async () => {
    // @ts-expect-error mock shape
    API.put = vi.fn().mockResolvedValue({ data: { budget: 1000 } })
    const result = await updateBudget({ amount: 1000 })
    expect(API.put).toHaveBeenCalledWith('/user/budget', { amount: 1000 })
    expect(result).toEqual({ budget: 1000 })
  })

  it('updateUserTheme PUTs theme', async () => {
    // @ts-expect-error mock shape
    API.put = vi.fn().mockResolvedValue({ data: { theme: Theme.DARK } })
    const result = await updateUserTheme({ theme: Theme.DARK })
    expect(API.put).toHaveBeenCalledWith('/user/theme', { theme: Theme.DARK })
    expect(result).toEqual({ theme: Theme.DARK })
  })
})
