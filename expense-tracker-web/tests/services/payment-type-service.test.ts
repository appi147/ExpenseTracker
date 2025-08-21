import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as svc from '../../src/services/payment-type-service'
import API from '../../src/services/api'

describe('services/payment-type-service', () => {
  beforeEach(() => {
    // @ts-expect-error
    API.get = vi.fn()
  })

  it('getAllPaymentTypes GETs and returns data', async () => {
    // @ts-expect-error
    API.get.mockResolvedValue({ data: [{ code: 'CASH', label: 'Cash' }] })
    const res = await svc.getAllPaymentTypes()
    expect(API.get).toHaveBeenCalledWith('/payment-types')
    expect(res[0].code).toBe('CASH')
  })
})
