import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { AuthProvider, useAuth } from '../../src/context/AuthContext'
import * as api from '../../src/services/api'

const Consumer = () => {
  const { token, setAuthToken, user, logout } = useAuth()
  return (
    <div>
      <div data-testid="token">{token ?? 'null'}</div>
      <div data-testid="user">{user ? user.fullName : 'no-user'}</div>
      <button onClick={() => setAuthToken('tok')}>set</button>
      <button onClick={() => logout()}>logout</button>
    </div>
  )
}

describe('context/AuthContext', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.spyOn(api, 'getUserProfile').mockResolvedValue({
      fullName: 'John Doe',
      email: 'j@d.com',
      pictureUrl: '',
      role: 'USER',
      budget: 0,
      preferredTheme: 'LIGHT',
    } as any)
  })

  it('provides token and can set it', async () => {
    render(
      <AuthProvider>
        <Consumer />
      </AuthProvider>
    )

    expect(screen.getByTestId('token').textContent).toBe('null')
    screen.getByText('set').click()
    await waitFor(() => expect(screen.getByTestId('token').textContent).toBe('tok'))
  })

  it('fetches user profile when token present', async () => {
    render(
      <AuthProvider>
        <Consumer />
      </AuthProvider>
    )

    screen.getByText('set').click()
    await waitFor(() => expect(screen.getByTestId('user').textContent).toBe('John Doe'))
  })

  it('logout clears token and user', async () => {
    render(
      <AuthProvider>
        <Consumer />
      </AuthProvider>
    )

    screen.getByText('set').click()
    await waitFor(() => expect(screen.getByTestId('user').textContent).toBe('John Doe'))
    screen.getByText('logout').click()
    await waitFor(() => expect(screen.getByTestId('token').textContent).toBe('null'))
    expect(screen.getByTestId('user').textContent).toBe('no-user')
  })

  it('handles profile fetch error by logging out', async () => {
    ;(api.getUserProfile as any).mockRejectedValueOnce(new Error('bad'))
    render(
      <AuthProvider>
        <Consumer />
      </AuthProvider>
    )

    screen.getByText('set').click()
    await waitFor(() => expect(screen.getByTestId('token').textContent).toBe('null'))
    expect(screen.getByTestId('user').textContent).toBe('no-user')
  })
})
