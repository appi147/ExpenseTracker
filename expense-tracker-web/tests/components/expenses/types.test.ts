import { describe, it, expect } from 'vitest'
import type { Expense } from '../../../src/components/expenses/types'

describe('components/expenses/types', () => {
  it('type Expense structure sanity', () => {
    const e: Expense = {
      expenseId: 1,
      amount: 1,
      date: '2024-01-01',
      paymentType: { code: 'CARD', label: 'Card' },
      subCategory: { subCategoryId: 2, label: 'a', category: { categoryId: 3, label: 'b' } },
    }
    expect(e.expenseId).toBe(1)
  })
})
