import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import Dashboard from '../../src/pages/Dashboard'
import * as expSvc from '../../src/services/expense-service'
import { MemoryRouter } from 'react-router-dom'

describe('pages/Dashboard', () => {
  it('loads monthly totals and toggles duration', async () => {
    vi.spyOn(expSvc, 'getMonthlyExpense').mockResolvedValue({ last30Days: 20, currentMonth: 10 })

    render(
      <MemoryRouter>
        <Dashboard />
      </MemoryRouter>
    )

    await screen.findByText('₹10.00')
    fireEvent.click(screen.getByRole('switch'))
    await screen.findByText('₹20.00')
  })
})
