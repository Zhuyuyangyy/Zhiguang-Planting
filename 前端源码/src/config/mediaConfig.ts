/**
 * MinIO 媒体资源统一配置
 * 解决 Mixed Content 问题：所有路径走 Nginx HTTPS 代理
 */

// MinIO 基础路径（走 Nginx 代理）
const MINIO_BASE = '/minio/dental-robot-bucket';

export const MEDIA = {
  // 视频路径
  video: (filename: string) => `${MINIO_BASE}/model/video/${filename}`,
  
  // 模型路径
  model: (filename: string) => `${MINIO_BASE}/models/${filename}`,
  
  // 直接获取完整路径
  getUrl: (path: string) => `${MINIO_BASE}/${path}`,
};

// API 基础路径（相对路径，走 Nginx 代理）
export const API = {
  base: '',
  // 模型相关
  modelVideo: '/api/v1/model/video',
};