import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

const apiTarget = process.env.VITE_PROXY_API_TARGET || 'http://localhost:8253'
const minioTarget = process.env.VITE_PROXY_MINIO_TARGET || 'http://localhost:9000'

export default defineConfig({
  base: './',
  plugins: [vue()],
  server: {
    port: 8254,
    host: '0.0.0.0',
    proxy: {
      '/api/v1': {
        target: apiTarget,
        changeOrigin: true,
        rewrite: (requestPath) => requestPath.replace(/^\/api/, ''),
      },
      '/api/avatar': {
        target: apiTarget,
        changeOrigin: true,
      },
      '/minio': {
        target: minioTarget,
        changeOrigin: true,
        rewrite: (requestPath) => requestPath.replace(/^\/minio/, ''),
      },
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
})
