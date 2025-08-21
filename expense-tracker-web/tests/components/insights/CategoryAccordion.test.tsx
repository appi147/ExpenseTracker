import React from 'react'
import { describe, it, expect } from 'vitest'
import { render, screen, act } from '@testing-library/react'
import '@testing-library/jest-dom'
import CategoryAccordion from '../../../src/components/insights/CategoryAccordion'

describe('components/insights/CategoryAccordion', () => {
  it('renders categories and subcategories amounts', async () => {
    render(
      <CategoryAccordion
        data={[
          {
            category: 'Food',
            amount: 30,
            subCategoryWiseExpenses: [
              { subCategory: 'Lunch', amount: 20 },
              { subCategory: 'Dinner', amount: 10 },
            ],
          },
        ]}
      />
    )
  
      expect(screen.getByText('Food')).toBeInTheDocument()
      expect(screen.getByText('₹30.00')).toBeInTheDocument()
  
      const trigger = screen.getByRole('button', { name: /Food/i })
      await act(async () => { trigger.click() })
  
      expect(await screen.findByText('Lunch')).toBeInTheDocument()
    })
  
})
