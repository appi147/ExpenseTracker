
import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

const SRC_DIR = path.join(__dirname, 'src')
const TESTS_DIR = path.join(__dirname, 'tests')

function isTestableFile(file) {
  return (
    (file.endsWith('.ts') || file.endsWith('.tsx')) &&
    !file.endsWith('.d.ts') &&
    !file.includes('.test.') &&
    !file.includes('.spec.')
  )
}

function getTestPath(filePath) {
  const relative = path.relative(SRC_DIR, filePath)
  const testPath = path.join(TESTS_DIR, relative)

  const parsed = path.parse(testPath)
  return path.join(parsed.dir, `${parsed.name}.test${parsed.ext}`)
}

function generateTestSkeleton(sourceFile, testFile) {
  const importPath = path
    .relative(path.dirname(testFile), sourceFile)
    .replace(/\\/g, '/')
    .replace(/\.tsx?$/, '')

  const isTSX = sourceFile.endsWith('.tsx')

  const content = `import { describe, it, expect } from 'vitest'
${isTSX ? "import { render, screen } from '@testing-library/react'" : ''}
import * as Module from '${importPath}'

describe('${path.basename(sourceFile)}', () => {
  it('should have tests', () => {
    expect(true).toBe(true)
  })
})
`

  fs.mkdirSync(path.dirname(testFile), { recursive: true })
  fs.writeFileSync(testFile, content)
  console.log('✅ Created test:', testFile)
}

function walk(dir) {
  const entries = fs.readdirSync(dir, { withFileTypes: true })
  for (const entry of entries) {
    const fullPath = path.join(dir, entry.name)
    if (entry.isDirectory()) {
      walk(fullPath)
    } else if (isTestableFile(fullPath)) {
      const testFile = getTestPath(fullPath)
      if (!fs.existsSync(testFile)) {
        generateTestSkeleton(fullPath, testFile)
      }
    }
  }
}

walk(SRC_DIR)
console.log('✅ Test generation completed.')
