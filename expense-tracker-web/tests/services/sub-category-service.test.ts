import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as svc from '../../src/services/sub-category-service'
import API from '../../src/services/api'

describe('services/sub-category-service', () => {
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

  it('getAllSubCategories passes categoryId via params', async () => {
    // @ts-expect-error
    API.get.mockResolvedValue({ data: [] })
    const res = await svc.getAllSubCategories(5)
    expect(API.get).toHaveBeenCalledWith('/sub-category', { params: { categoryId: 5 } })
    expect(res).toEqual([])
  })

  it('createSubCategory posts payload', async () => {
    // @ts-expect-error
    API.post.mockResolvedValue({ data: { subCategoryId: 10 } })
    const res = await svc.createSubCategory({ label: 'Lunch', categoryId: 1 })
    expect(API.post).toHaveBeenCalledWith('/sub-category/create', { label: 'Lunch', categoryId: 1 })
    expect(res.subCategoryId).toBe(10)
  })

  it('updateSubCategory puts payload', async () => {
    // @ts-expect-error
    API.put.mockResolvedValue({ data: { subCategoryId: 10, label: 'Dinner' } })
    const res = await svc.updateSubCategory(10, { label: 'Dinner' })
    expect(API.put).toHaveBeenCalledWith('/sub-category/10', { label: 'Dinner' })
    expect(res.label).toBe('Dinner')
  })

  it('deleteSubCategory invokes delete', async () => {
    // @ts-expect-error
    API.delete.mockResolvedValue({})
    await svc.deleteSubCategory(3)
    expect(API.delete).toHaveBeenCalledWith('/sub-category/3')
  })
})
