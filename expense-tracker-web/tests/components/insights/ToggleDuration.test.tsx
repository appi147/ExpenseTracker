import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import ToggleDuration from '../../../src/components/insights/ToggleDuration'

describe('components/insights/ToggleDuration', () => {
  it('renders and triggers onChange', () => {
    const onChange = vi.fn()
    render(<ToggleDuration monthly={true} onChange={onChange} />)
    const sw = screen.getByRole('switch')
    fireEvent.click(sw)
    expect(onChange).toHaveBeenCalled()
  })
})
