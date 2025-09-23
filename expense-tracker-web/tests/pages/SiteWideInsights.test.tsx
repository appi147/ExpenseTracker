import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import SiteWideInsights from '../../src/pages/SiteWideInsights'

vi.mock('../../src/services/insight-service', () => ({
  getInsight: vi.fn().mockResolvedValue({
    totalUsersRegistered: 10,
    totalUsersAddedExpense: 7,
    totalExpensesAdded: 12345,
    totalTransactionsAdded: 20,
    totalCategoriesCreated: 5,
    totalSubCategoriesCreated: 9,
  })
}))

describe('SiteWideInsights.tsx', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders cards with fetched values', async () => {
    render(<SiteWideInsights />)
    expect(await screen.findByText('Site-wide Insights')).toBeInTheDocument()
    expect(await screen.findByText('Users Registered')).toBeInTheDocument()
    expect(await screen.findByText('10')).toBeInTheDocument()
  })
})
