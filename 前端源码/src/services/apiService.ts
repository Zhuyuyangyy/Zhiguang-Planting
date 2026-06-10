//AI辅助生成：豆包,2026-03-27 API服务层搭建
/**
 * @author 朱玉阳
 * @version 1.0
 * @date 2026/04/08
 * */
import { MEDIA } from '../config/mediaConfig';

class ApiService {
    // 后端API地址（使用相对路径，走 Nginx 代理）
    private baseUrl = '';

    async request<T>(options: {
        url: string;
        method: 'GET' | 'POST' | 'PUT' | 'DELETE';
        data?: Record<string, any>;
    }): Promise<T> {
        try {
            const url = this.baseUrl + options.url;
            const fetchOptions: RequestInit = {
                method: options.method,
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'omit',
                mode: 'cors',
            };

            if (options.data) {
                fetchOptions.body = JSON.stringify(options.data);
            }

            const response = await fetch(url, fetchOptions);
            const result = await response.json();

            if (!response.ok) {
                throw new Error(`请求失败：${response.status}`);
            }
            return result as T;
        } catch (error) {
            console.error('API 请求失败：', error);
            throw error;
        }
    }

    async post<T>(url: string, data: any) {
        return this.request<T>({ url, method: 'POST', data });
    }

    // ==================== 模型下载（直连 MinIO） ====================
    async downloadModel(
        productId: string,
        format = 'glb',
        onProgress?: (percent: number) => void
    ): Promise<Blob> {
        return new Promise((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            // 使用统一配置获取模型路径
            const url = MEDIA.model(`${productId}.${format}`);

            xhr.open('GET', url, true);
            xhr.responseType = 'blob';

            // 进度条
            xhr.onprogress = (e) => {
                if (e.lengthComputable && onProgress) {
                    const p = Math.round((e.loaded / e.total) * 100);
                    onProgress(p);
                }
            };

            xhr.onload = () => {
                if (xhr.status >= 200 && xhr.status < 300) {
                    resolve(xhr.response);
                } else {
                    reject('模型加载失败：' + xhr.status);
                }
            };

            xhr.onerror = () => reject('网络异常，模型加载失败');
            xhr.send();
        });
    }

    // ==================== 模型上传（直连 MinIO） ====================
    async uploadModel(
        file: File,
        productId: string,
        format = 'glb',
        onProgress?: (percent: number) => void
    ): Promise<void> {
        return new Promise((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            // 使用统一配置获取模型路径
            const url = MEDIA.model(`${productId}.${format}`);

            xhr.open('PUT', url, true);

            // 进度条
            xhr.upload.onprogress = (e) => {
                if (e.lengthComputable && onProgress) {
                    const p = Math.round((e.loaded / e.total) * 100);
                    onProgress(p);
                }
            };

            xhr.onload = () => {
                if (xhr.status >= 200 && xhr.status < 300) {
                    resolve();
                } else {
                    reject(new Error('模型上传失败：' + xhr.status));
                }
            };

            xhr.onerror = () => reject(new Error('网络异常，模型上传失败'));
            xhr.send(file);
        });
    }
}

export default new ApiService();