import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import Layout from '../../src/components/Layout'
import { AuthProvider } from '../../src/context/AuthContext'
import { MemoryRouter } from 'react-router-dom'

describe('components/Layout', () => {
  it('renders navbar and children', () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <Layout>
            <div>Child</div>
          </Layout>
        </AuthProvider>
      </MemoryRouter>
    )
    expect(screen.getByText('Expense Tracker')).toBeInTheDocument()
    expect(screen.getByText('Child')).toBeInTheDocument()
  })
})
