import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import ProtectedRoute from '../../src/components/ProtectedRoute'
import { AuthProvider } from '../../src/context/AuthContext'
import * as api from '../../src/services/api'

const Wrapper: React.FC<{ token?: string }> = ({ token }) => (
  <AuthProvider>
    <Routes>
      <Route path="/" element={
        <ProtectedRoute>
          <div>Home</div>
        </ProtectedRoute>
      } />
      <Route path="/login" element={<div>Login</div>} />
    </Routes>
  </AuthProvider>
)

describe('components/ProtectedRoute', () => {
  it('redirects to login when not authenticated', () => {
    render(
      <MemoryRouter initialEntries={["/"]}>
        <Wrapper />
      </MemoryRouter>
    )
    expect(screen.getByText('Login')).toBeInTheDocument()
  })

  it('renders children when authenticated', () => {
    // prevent API call within AuthProvider from hitting network
    vi.spyOn(api, 'getUserProfile').mockResolvedValue({} as any)
    localStorage.setItem('auth_token', 'tok')
    render(
      <MemoryRouter initialEntries={["/"]}>
        <Wrapper />
      </MemoryRouter>
    )
    expect(screen.getByText('Home')).toBeInTheDocument()
  })
})
