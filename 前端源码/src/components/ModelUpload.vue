<!--@author 朱玉阳-->
<!--上传3D模型页面-->
<template>
  <div class="upload-page">
    <div class="upload-card">
      <h2>上传 3D 模型</h2>

      <div class="form-item">
        <label>产品 ID</label>
        <input v-model="productId" placeholder="如：1002" />
      </div>

      <div class="form-item">
        <label>文件格式</label>
        <select v-model="format">
          <option value="glb">GLB</option>
          <option value="gltf">GLTF</option>
        </select>
      </div>

      <!-- 拖拽上传区域 -->
      <div
          class="drop-zone"
          :class="{ dragover: isDragging, 'has-file': selectedFile }"
          @dragover.prevent="isDragging = true"
          @dragleave="isDragging = false"
          @drop.prevent="onDrop"
          @click="fileInput?.click()"
      >
        <input ref="fileInput" type="file" accept=".glb,.gltf" style="display:none" @change="onFileChange" />
        <div v-if="!selectedFile">
          <p>点击或拖拽文件到此处</p>
          <span>.glb / .gltf</span>
        </div>
        <div v-else class="file-info">
          <p>{{ selectedFile.name }}</p>
          <span>{{ (selectedFile.size / 1024 / 1024).toFixed(1) }} MB</span>
        </div>
      </div>

      <!-- 上传进度 -->
      <div v-if="uploading" class="progress-bar">
        <div class="progress-fill" :style="{ width: progress + '%' }"></div>
        <span>{{ progress }}%</span>
      </div>

      <!-- 提示消息 -->
      <div v-if="message" class="message" :class="success ? 'success' : 'error'">
        {{ message }}
      </div>

      <button :disabled="uploading || !selectedFile || !productId" @click="handleUpload">
        {{ uploading ? '上传中...' : '开始上传' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import modelService from '../services/modelService';

const productId = ref('');
const format = ref('glb');
const selectedFile = ref<File | null>(null);
const fileInput = ref<HTMLInputElement | null>(null);
const isDragging = ref(false);
const uploading = ref(false);
const progress = ref(0);
const message = ref('');
const success = ref(false);

// 选文件
const onFileChange = (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0];
  if (file) selectedFile.value = file;
};

// 拖进来
const onDrop = (e: DragEvent) => {
  isDragging.value = false;
  const file = e.dataTransfer?.files?.[0];
  if (file) selectedFile.value = file;
};

//AI辅助生成：豆包,2026-04-02 文件上传逻辑
const handleUpload = async () => {
  if (!selectedFile.value || !productId.value) return;
  uploading.value = true;
  message.value = '';
  progress.value = 0;

  try {
    await modelService.uploadModel(selectedFile.value, productId.value, format.value, (p) => {
      progress.value = p;
    });
    success.value = true;
    message.value = '上传成功！';
    selectedFile.value = null;
  } catch (err: any) {
    success.value = false;
    message.value = '上传失败：' + (err.message || '未知错误');
  } finally {
    uploading.value = false;
  }
};
</script>

<style scoped>
.upload-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
}
.upload-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  width: 480px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  gap: 20px;
}
h2 { margin: 0; font-size: 22px; color: #1a1a2e; }
.form-item { display: flex; flex-direction: column; gap: 6px; }
.form-item label { font-size: 13px; color: #666; }
.form-item input, .form-item select {
  padding: 10px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}
.form-item input:focus, .form-item select:focus { border-color: #667eea; }
.drop-zone {
  border: 2px dashed #d0d0d0;
  border-radius: 10px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  color: #999;
}
.drop-zone:hover, .drop-zone.dragover { border-color: #667eea; background: #f5f3ff; color: #667eea; }
.drop-zone.has-file { border-color: #52c41a; background: #f6ffed; color: #389e0d; }
.drop-zone p { margin: 0 0 4px; font-size: 15px; }
.drop-zone span { font-size: 12px; }
.file-info p { font-weight: 600; }
.progress-bar {
  position: relative;
  height: 24px;
  background: #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transition: width 0.3s ease;
  border-radius: 12px;
}
.progress-bar span {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 12px;
  font-weight: 600;
  color: white;
}
.message { padding: 10px 16px; border-radius: 8px; font-size: 14px; }
.message.success { background: #f6ffed; color: #389e0d; border: 1px solid #b7eb8f; }
.message.error { background: #fff2f0; color: #cf1322; border: 1px solid #ffa39e; }
button {
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: opacity 0.2s;
}
button:disabled { opacity: 0.5; cursor: not-allowed; }
button:not(:disabled):hover { opacity: 0.9; }
</style>
