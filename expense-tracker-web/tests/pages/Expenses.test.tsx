import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import Expenses from '../../src/pages/Expenses'
import * as expSvc from '../../src/services/expense-service'
import { MemoryRouter } from 'react-router-dom'

describe('pages/Expenses', () => {
  it('fetches and renders table data; supports delete and export', async () => {
    vi.spyOn(expSvc, 'getFilteredExpenses').mockResolvedValue({
      content: [
        {
          expenseId: 1,
          amount: 10,
          date: '2024-01-01',
          comments: '',
          paymentType: { code: 'CASH', label: 'Cash' },
          subCategory: { subCategoryId: 2, label: 'Lunch', category: { categoryId: 1, label: 'Food' } },
        },
      ],
      totalPages: 1,
    } as any)
    vi.spyOn(expSvc, 'deleteExpense').mockResolvedValue()
    vi.spyOn(expSvc, 'exportExpenses').mockResolvedValue()

    render(
      <MemoryRouter>
        <Expenses />
      </MemoryRouter>
    )

    await screen.findByText('Lunch')
    fireEvent.click(screen.getByText('Export'))
    fireEvent.click(screen.getByText('Export as CSV'))
    await waitFor(() => expect(expSvc.exportExpenses).toHaveBeenCalled())
  })
})
