import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { CustomTooltip } from '../../../src/components/trends/CustomTooltip'

describe('CustomTooltip.tsx', () => {
  it('renders entries when active with payload', () => {
    render(
      <CustomTooltip
        active
        label="Jan 2024"
        payload={[{ name: 'Food', value: 100, color: '#f00', stroke: '#f00' } as any]}
      />
    )
    expect(screen.getByText('Jan 2024')).toBeInTheDocument()
    expect(screen.getByText('Food')).toBeInTheDocument()
    expect(screen.getByText('100')).toBeInTheDocument()
  })

  it('returns null when not active', () => {
    const { container } = render(<CustomTooltip active={false} payload={[]} />)
    expect(container).toBeEmptyDOMElement()
  })
})
