import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as svc from '../../src/services/category-service'
import API from '../../src/services/api'

describe('services/category-service', () => {
  beforeEach(() => {
    // @ts-expect-error
    API.get = vi.fn()
    // @ts-expect-error
    API.post = vi.fn()
    // @ts-expect-error
    API.put = vi.fn()
    // @ts-expect-error
    API.delete = vi.fn()
  })

  it('getAllCategories returns data', async () => {
    // @ts-expect-error
    API.get.mockResolvedValue({ data: [{ categoryId: 1, label: 'Food', deletable: true }] })
    const res = await svc.getAllCategories()
    expect(API.get).toHaveBeenCalledWith('/category')
    expect(res[0].label).toBe('Food')
  })

  it('createCategory posts payload', async () => {
    // @ts-expect-error
    API.post.mockResolvedValue({ data: { categoryId: 2 } })
    const res = await svc.createCategory({ label: 'Travel' })
    expect(API.post).toHaveBeenCalledWith('/category/create', { label: 'Travel' })
    expect(res.categoryId).toBe(2)
  })

  it('updateCategory puts payload', async () => {
    // @ts-expect-error
    API.put.mockResolvedValue({ data: { categoryId: 1, label: 'Food & Dining' } })
    const res = await svc.updateCategory(1, { label: 'Food & Dining' })
    expect(API.put).toHaveBeenCalledWith('/category/1', { label: 'Food & Dining' })
    expect(res.label).toContain('Food')
  })

  it('deleteCategory invokes delete', async () => {
    // @ts-expect-error
    API.delete.mockResolvedValue({})
    await svc.deleteCategory(9)
    expect(API.delete).toHaveBeenCalledWith('/category/9')
  })
})
