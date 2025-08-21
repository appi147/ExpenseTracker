import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as svc from '../../src/services/insight-service'
import API from '../../src/services/api'

describe('services/insight-service', () => {
  beforeEach(() => {
    // @ts-expect-error
    API.get = vi.fn()
  })

  it('getInsight calls /insights/site-wide', async () => {
    // @ts-expect-error
    API.get.mockResolvedValue({ data: { totalUsersRegistered: 1 } })
    const res = await svc.getInsight()
    expect(API.get).toHaveBeenCalledWith('/insights/site-wide')
    expect(res.totalUsersRegistered).toBe(1)
  })

  it('getMonthlyTrends calls /insights/monthly-trends', async () => {
    // @ts-expect-error
    API.get.mockResolvedValue({ data: [] })
    const res = await svc.getMonthlyTrends()
    expect(API.get).toHaveBeenCalledWith('/insights/monthly-trends')
    expect(res).toEqual([])
  })
})
