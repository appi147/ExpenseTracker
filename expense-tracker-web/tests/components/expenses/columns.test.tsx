import { describe, it, expect, vi } from 'vitest'
import { render } from '@testing-library/react'
import { getExpenseColumns } from '../../../src/components/expenses/columns'
import type { Expense } from '../../../src/components/expenses/types'

const sample: Expense = {
  expenseId: 1,
  amount: 99.5,
  date: '2024-01-02',
  comments: 'note',
  paymentType: { code: 'CASH', label: 'Cash' },
  subCategory: {
    subCategoryId: 11,
    label: 'Lunch',
    category: { categoryId: 5, label: 'Food' },
  },
}

describe('components/expenses/columns', () => {
  it('renders amount with currency and triggers edit/delete', () => {
    const onEdit = vi.fn()
    const onDelete = vi.fn()
    const cols = getExpenseColumns(onEdit, onDelete)
    const amountCol = cols.find(c => c.accessorKey === 'amount')!
    // @ts-expect-error render cell fn signature
    const { getByText } = render(amountCol.cell({ row: { original: sample } }))
    expect(getByText('₹99.5')).toBeInTheDocument()

    const actionsCol: any = cols.find(c => (c as any).id === 'actions')
    const { container } = render(actionsCol.cell({ row: { original: sample } }))
    const btn = container.querySelector('button') as HTMLButtonElement
    btn.click()
    expect(onDelete).toHaveBeenCalledWith(1)
  })
})
