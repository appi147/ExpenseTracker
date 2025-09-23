import { describe, it, expect } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import SubCategoryItem from '../../../src/components/categories/SubCategoryItem'

describe('SubCategoryItem.tsx', () => {
  const baseSub = { subCategoryId: 1, label: 'Coffee', deletable: true }

  it('toggles edit state and saves updated label', () => {
    const onUpdate = vi.fn()
    const onDelete = vi.fn()

    const { container } = render(
      <SubCategoryItem sub={baseSub} categoryId={123} onUpdate={onUpdate} onDelete={onDelete} />
    )

    const pencil = container.querySelector('svg.lucide-pencil') as SVGElement
    fireEvent.click(pencil)
    const input = screen.getByDisplayValue('Coffee') as HTMLInputElement
    fireEvent.change(input, { target: { value: 'Beans' } })
    fireEvent.click(screen.getByRole('button', { name: /save/i }))

    expect(onUpdate).toHaveBeenCalledWith(1, 'Beans')
  })

  it('calls onDelete when trash icon clicked if deletable', () => {
    const onUpdate = vi.fn()
    const onDelete = vi.fn()
    const { container } = render(
      <SubCategoryItem sub={baseSub} categoryId={123} onUpdate={onUpdate} onDelete={onDelete} />
    )
    const trash = container.querySelector('svg.lucide-trash2') as SVGElement
    fireEvent.click(trash)
    expect(onDelete).toHaveBeenCalledWith(1)
  })
})
