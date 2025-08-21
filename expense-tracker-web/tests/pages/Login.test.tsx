import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import Login from '../../src/pages/Login'
import { AuthProvider } from '../../src/context/AuthContext'
import { MemoryRouter } from 'react-router-dom'

vi.mock('@react-oauth/google', () => ({
  GoogleLogin: (props: any) => <button onClick={() => props.onSuccess({ credential: 'tok' })}>Google</button>,
  useGoogleOneTapLogin: () => {},
}))

describe('pages/Login', () => {
  it('renders and can set token on success', async () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <Login />
        </AuthProvider>
      </MemoryRouter>
    )
    screen.getByText('Google').click()
    expect(localStorage.getItem('auth_token')).toBe('tok')
  })
})
