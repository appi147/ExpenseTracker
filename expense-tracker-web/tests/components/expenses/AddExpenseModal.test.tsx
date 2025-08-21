import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { AddExpenseModal } from '../../../src/components/expenses/AddExpenseModal'
import * as paySvc from '../../../src/services/payment-type-service'
import * as catSvc from '../../../src/services/category-service'
import * as subSvc from '../../../src/services/sub-category-service'
import * as expSvc from '../../../src/services/expense-service'
import { MemoryRouter } from 'react-router-dom'

describe('components/expenses/AddExpenseModal', () => {
  beforeEach(() => {
    vi.spyOn(paySvc, 'getAllPaymentTypes').mockResolvedValue([{ code: 'CASH', label: 'Cash' }])
    vi.spyOn(catSvc, 'getAllCategories').mockResolvedValue([{ categoryId: 1, label: 'Food', deletable: true }])
    vi.spyOn(subSvc, 'getAllSubCategories').mockResolvedValue([{ subCategoryId: 2, label: 'Lunch', deletable: true }])
    vi.spyOn(expSvc, 'createExpense').mockResolvedValue({})
  })

  it('loads options, validates required fields and submits', async () => {
    const onClose = vi.fn()
    const onAdded = vi.fn()
    render(
      <MemoryRouter>
        <AddExpenseModal isOpen={true} onClose={onClose} onExpenseAdded={onAdded} />
      </MemoryRouter>
    )

    // wait for dialog title
    await screen.findByRole('heading', { name: /Add Expense/i })

    // choose category -> loads subs
    const categoryTrigger = screen.getByText('Select a category')
    fireEvent.click(categoryTrigger)
    const foodOptions = await screen.findAllByText('Food')
    fireEvent.click(foodOptions[foodOptions.length - 1])

    const subTrigger = await screen.findByText('Select a subcategory')
    fireEvent.click(subTrigger)
    const lunchOptions = await screen.findAllByText('Lunch')
    fireEvent.click(lunchOptions[lunchOptions.length - 1])

    const payTrigger = screen.getByText('Select a payment method')
    fireEvent.click(payTrigger)
    const cashOptions = await screen.findAllByText('Cash')
    fireEvent.click(cashOptions[cashOptions.length - 1])

    // The input may not be label-associated in DOM; fallback by role
    const amount = (screen.getByRole('textbox', { name: /Amount/i }) || screen.getAllByRole('textbox')[0]) as HTMLInputElement
    fireEvent.change(amount, { target: { value: '123.45' } })

    const submitBtn = screen.getAllByText('Add Expense').find(el => el.tagName === 'BUTTON') as HTMLElement
    fireEvent.click(submitBtn)

    await waitFor(() => expect(expSvc.createExpense).toHaveBeenCalled())
    expect(onClose).toHaveBeenCalled()
    expect(onAdded).toHaveBeenCalled()
  })
})
