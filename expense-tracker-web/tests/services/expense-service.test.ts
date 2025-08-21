import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as service from '../../src/services/expense-service'
import API from '../../src/services/api'

describe('services/expense-service', () => {
  beforeEach(() => {
    // @ts-expect-error swap methods
    API.get = vi.fn()
    // @ts-expect-error swap methods
    API.post = vi.fn()
    // @ts-expect-error swap methods
    API.put = vi.fn()
    // @ts-expect-error swap methods
    API.delete = vi.fn()
  })

  it('createExpense POSTs and returns data', async () => {
    // @ts-expect-error
    API.post.mockResolvedValue({ data: { id: 1 } })
    const res = await service.createExpense({
      amount: 10,
      date: '2024-01-01',
      comments: 'c',
      subCategoryId: 2,
      paymentTypeCode: 'CASH',
      monthsToAmortize: 1,
    })
    expect(API.post).toHaveBeenCalledWith('/expense/create', expect.any(Object))
    expect(res).toEqual({ id: 1 })
  })

  it('getMonthlyExpense GETs and returns numbers', async () => {
    // @ts-expect-error
    API.get.mockResolvedValue({ data: { last30Days: 5, currentMonth: 7 } })
    const res = await service.getMonthlyExpense()
    expect(API.get).toHaveBeenCalledWith('/expense/monthly')
    expect(res).toEqual({ last30Days: 5, currentMonth: 7 })
  })

  it('getFilteredExpenses builds params with defaults', async () => {
    // @ts-expect-error
    API.get.mockResolvedValue({ data: { content: [], totalPages: 0 } })
    const res = await service.getFilteredExpenses({ page: 0, size: 10 })
    expect(API.get).toHaveBeenCalledWith(expect.stringContaining('/expense/list?'))
    expect(res).toEqual({ content: [], totalPages: 0 })
  })

  it('deleteExpense calls delete', async () => {
    // @ts-expect-error
    API.delete.mockResolvedValue({})
    await service.deleteExpense(1)
    expect(API.delete).toHaveBeenCalledWith('/expense/1')
  })

  it('updateExpenseAmount calls PUT with body', async () => {
    // @ts-expect-error
    API.put.mockResolvedValue({ status: 200 })
    await service.updateExpenseAmount(3, 45.5)
    expect(API.put).toHaveBeenCalledWith('/expense/3/amount', { amount: 45.5 })
  })

  it('getMonthlyInsight respects monthly flag', async () => {
    // @ts-expect-error
    API.get.mockResolvedValue({ data: { totalExpense: 0, monthlyBudget: 0, categoryWiseExpenses: [] } })
    await service.getMonthlyInsight(true)
    expect(API.get).toHaveBeenCalledWith('/expense/insight?monthly=true')
    await service.getMonthlyInsight(false)
    expect(API.get).toHaveBeenCalledWith('/expense/insight?monthly=false')
  })
})
