import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import ExpenseInsight from '../../../src/components/insights/ExpenseInsight'
import * as expSvc from '../../../src/services/expense-service'

describe('components/insights/ExpenseInsight', () => {
  it('shows empty state when no data', async () => {
    vi.spyOn(expSvc, 'getMonthlyInsight').mockResolvedValue({
      totalExpense: 0,
      monthlyBudget: 0,
      categoryWiseExpenses: []
    } as any);

    render(<ExpenseInsight initialMonthly={true} />);

    // assertion that empty state text appears
    expect(await screen.findByText('No expenses found')).toBeInTheDocument();

    // optional extra assertion: ensure chart or list is not rendered
    expect(screen.queryByTestId('category-expenses-chart')).not.toBeInTheDocument();
  });

  it('renders budget and can toggle monthly flag', async () => {
    vi.spyOn(expSvc, 'getMonthlyInsight').mockResolvedValue({
      totalExpense: 100,
      monthlyBudget: 200,
      categoryWiseExpenses: [
        { category: 'Food', amount: 100, subCategoryWiseExpenses: [{ subCategory: 'Lunch', amount: 100 }] },
      ],
    } as any)
    render(<ExpenseInsight initialMonthly={true} />)
    await screen.findByText(/Total Expense/i)
    const toggle = screen.getByRole('switch')
    fireEvent.click(toggle)
    await waitFor(() => expect(expSvc.getMonthlyInsight.mock.calls.length).toBeGreaterThanOrEqual(2))
  })
})
