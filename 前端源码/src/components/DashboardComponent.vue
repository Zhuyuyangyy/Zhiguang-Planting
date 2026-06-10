<!--
 * @author  刘宏伟
 * @version 1.0
 * @date    2026/04/08
 * @description 主页
-->
<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import * as echarts from 'echarts';

if (typeof window !== 'undefined') {
  window.echarts = echarts;
}

// 本地视频路径（public 文件夹下的视频）
const videoSrc = '/1003.mp4';

const laserChartRef = ref(null);
const successChartRef = ref(null);
const efficiencyChartRef = ref(null);
const satisfactionChartRef = ref(null);
const aiChartRef = ref(null);

const chartInstances = [];

const techCards = [
  { icon: 'LAS', title: '激光波长', value: '3000nm', note: '水吸收效率 > 90%' },
  { icon: 'ARM', title: '机械臂精度', value: '±0.02mm', note: '活动半径 800mm' },
  { icon: 'CBCT', title: 'CBCT 分辨率', value: '≤ 0.1mm', note: '扫描时间 < 10s' },
  { icon: 'NAV', title: '光学追踪', value: '100Hz', note: '延迟 < 5ms' },
];

const advantageRows = [
  { id: 1, label: '定位精度', traditional: '2mm', robot: '0.3mm', improve: '85%' },
  { id: 2, label: '手术时间', traditional: '60分钟', robot: '40分钟', improve: '33%' },
  { id: 3, label: '感染率', traditional: '3-5%', robot: '0.5%', improve: '90%' },
  { id: 4, label: '骨结合时间', traditional: '3-6个月', robot: '1-3个月', improve: '50%' },
  { id: 5, label: '创伤面积', traditional: '100%', robot: '40%', improve: '60%' },
  { id: 6, label: '出血量', traditional: '100%', robot: '30%', improve: '70%' },
  { id: 7, label: '新手成功率', traditional: '60%', robot: '95%', improve: '58%' },
];

const axisLineColor = 'rgba(0, 255, 255, 0.4)';
const splitLineColor = 'rgba(0, 255, 255, 0.12)';
const axisLabelColor = '#86f7ff';
const legendTextStyle = { color: '#86f7ff' };

function ensureLegacyChartStubs() {
  if (typeof document === 'undefined') {
    return;
  }

  let container = document.getElementById('legacy-dashboard-echarts-stubs');
  if (!container) {
    container = document.createElement('div');
    container.id = 'legacy-dashboard-echarts-stubs';
    container.style.position = 'fixed';
    container.style.width = '1px';
    container.style.height = '1px';
    container.style.opacity = '0';
    container.style.pointerEvents = 'none';
    container.style.overflow = 'hidden';
    container.style.left = '-9999px';
    container.style.top = '-9999px';
    document.body.appendChild(container);
  }

  ['gaugeChart', 'trendChart'].forEach((id) => {
    if (!document.getElementById(id)) {
      const node = document.createElement('div');
      node.id = id;
      node.style.width = '1px';
      node.style.height = '1px';
      container.appendChild(node);
    }
  });
}

function initChart(element, option) {
  if (!element) {
    return null;
  }

  const instance = echarts.init(element);
  instance.setOption(option);
  chartInstances.push(instance);
  return instance;
}
//AI辅助生成：豆包,2026-04-05 ECharts雷达图配置
function createGradient(colorStart, colorEnd) {
  return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
    { offset: 0, color: colorStart },
    { offset: 1, color: colorEnd },
  ]);
}
//AI辅助生成：豆包,2026-04-06 ECharts柱状图配置

//AI辅助生成：豆包,2026-04-05 ECharts图表配置
function setupCharts() {
  initChart(laserChartRef.value, {
    tooltip: { trigger: 'axis' },
    legend: {
      data: ['智能激光系统', '传统机械钻'],
      textStyle: legendTextStyle,
      top: 0,
    },
    grid: { left: '4%', right: '5%', bottom: '5%', top: 40, containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['精度', '热损伤', '效率', '安全性', '微创性'],
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: axisLabelColor },
      splitLine: { show: true, lineStyle: { color: splitLineColor, type: 'dashed' } },
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: axisLabelColor, formatter: '{value}%' },
      splitLine: { lineStyle: { color: splitLineColor, type: 'dashed' } },
    },
    series: [
      {
        name: '智能激光系统',
        type: 'line',
        smooth: true,
        data: [95, 90, 85, 98, 88],
        lineStyle: { color: '#00ffff', width: 2 },
        areaStyle: { color: createGradient('rgba(0,255,255,0.28)', 'rgba(0,255,255,0.04)') },
        symbolSize: 8,
      },
      {
        name: '传统机械钻',
        type: 'line',
        smooth: true,
        data: [60, 40, 65, 70, 50],
        lineStyle: { color: '#3ba0ff', width: 2 },
        areaStyle: { color: createGradient('rgba(59,160,255,0.22)', 'rgba(59,160,255,0.04)') },
        symbolSize: 8,
      },
    ],
  });

  initChart(successChartRef.value, {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: {
      data: ['机器人辅助', '传统手术'],
      textStyle: legendTextStyle,
      top: 5,
      right: '10%',
    },
    grid: { left: '8%', right: '8%', bottom: '12%', top: 40, containLabel: true },
    xAxis: {
      type: 'category',
      data: ['新手医生', '中级医生', '高级医生', '专家医生'],
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: {
        color: axisLabelColor,
        interval: 0,
        rotate: 0
      },
    },
    yAxis: {
      type: 'value',
      name: '成功率 (%)',
      nameTextStyle: {
        color: axisLabelColor,
        padding: [0, 0, 0, 10]
      },
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: axisLabelColor },
      splitLine: { lineStyle: { color: splitLineColor } },
    },
    series: [
      {
        name: '机器人辅助',
        type: 'bar',
        barMaxWidth: 16,
        data: [95, 98, 99, 99.5],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#00ffff' },
            { offset: 1, color: '#0066cc' }
          ])
        },
      },
      {
        name: '传统手术',
        type: 'bar',
        barMaxWidth: 16,
        data: [60, 80, 90, 95],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#4db9ff' },
            { offset: 1, color: '#003366' }
          ])
        },
      },
    ],
  });

  initChart(efficiencyChartRef.value, {
    tooltip: { trigger: 'axis' },
    legend: {
      data: ['机器人辅助', '传统手术'],
      textStyle: legendTextStyle,
      top: 0,
    },
    grid: { left: '4%', right: '5%', bottom: '8%', top: 36, containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['前牙区', '后牙区', '即刻种植', '全口种植', '上颌窦提升', '复杂病例'],
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: axisLabelColor, rotate: 28 },
    },
    yAxis: {
      type: 'value',
      name: '手术时间 (分钟)',
      nameTextStyle: { color: axisLabelColor },
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: axisLabelColor },
      splitLine: { lineStyle: { color: splitLineColor } },
    },
    series: [
      {
        name: '机器人辅助',
        type: 'line',
        smooth: true,
        data: [25, 30, 35, 60, 40, 50],
        lineStyle: { color: '#00ffff', width: 2 },
        symbolSize: 7,
      },
      {
        name: '传统手术',
        type: 'line',
        smooth: true,
        data: [45, 55, 60, 120, 80, 90],
        lineStyle: { color: '#0099ff', width: 2 },
        symbolSize: 7,
      },
    ],
  });

  initChart(satisfactionChartRef.value, {
    tooltip: { trigger: 'item' },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center',
      textStyle: legendTextStyle,
    },
    series: [
      {
        name: '满意度',
        type: 'pie',
        radius: ['42%', '72%'],
        center: ['62%', '54%'],
        label: { color: '#d8ffff', formatter: '{b}\n{d}%' },
        data: [
          { value: 85, name: '非常满意' },
          { value: 10, name: '满意' },
          { value: 4, name: '一般' },
          { value: 1, name: '不满意' },
        ],
        itemStyle: {
          color(params) {
            const gradients = [
              new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#00ffff' },
                { offset: 1, color: '#0066cc' }
              ]),
              new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#00c6ff' },
                { offset: 1, color: '#0052cc' }
              ]),
              new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#0d7bff' },
                { offset: 1, color: '#003399' }
              ]),
              new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: '#2852ff' },
                { offset: 1, color: '#1a33cc' }
              ])
            ];
            return gradients[params.dataIndex];
          },
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 18,
            shadowColor: 'rgba(0,255,255,0.45)',
          },
        },
      },
    ],
  });

  initChart(aiChartRef.value, {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: {
      data: ['机器人系统', '传统方法'],
      textStyle: legendTextStyle,
    },
    grid: { left: '4%', right: '5%', bottom: '6%', top: 36, containLabel: true },
    xAxis: {
      type: 'value',
      max: 100,
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: axisLabelColor },
      splitLine: { lineStyle: { color: splitLineColor } },
    },
    yAxis: {
      type: 'category',
      data: ['图像分割', '种植规划', '路径导航', '风险评估', '术后报告'],
      axisLine: { lineStyle: { color: axisLineColor } },
      axisLabel: { color: axisLabelColor },
    },
    series: [
      {
        name: '机器人系统',
        type: 'bar',
        barMaxWidth: 16,
        data: [98.7, 99.2, 99.5, 99.0, 100],
        itemStyle: { color: createGradient('#00ffff', '#0066cc') },
      },
      {
        name: '传统方法',
        type: 'bar',
        barMaxWidth: 16,
        data: [85, 70, 60, 75, 80],
        itemStyle: { color: createGradient('#4db9ff', '#003366') },
      },
    ],
  });
}

function handleResize() {
  chartInstances.forEach((chart) => chart.resize());
}

onMounted(() => {
  ensureLegacyChartStubs();
  setupCharts();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  chartInstances.forEach((chart) => chart.dispose());
  chartInstances.length = 0;
});
</script>

<template>
  <section class="dashboard-section">
    <div class="dashboard-shell">
      <div class="dashboard-header">
        <p class="dashboard-kicker">INTELLIGENT IMPLANT ROBOT</p>
        <h2 class="dashboard-title">智能激光口腔种植机器人数据中心</h2>
      </div>

      <div class="dashboard-grid">
        <article class="dashboard-panel">
          <div class="panel-title">激光系统性能对比</div>
          <div ref="laserChartRef" class="chart-box"></div>
        </article>

        <article class="dashboard-panel dashboard-panel--video">
          <div class="panel-title">设备展示</div>
          <div class="video-frame">
            <video class="device-video" autoplay loop muted playsinline>
              <source :src="videoSrc" type="video/mp4" />
              当前浏览器不支持视频播放。
            </video>
          </div>
        </article>

        <article class="dashboard-panel">
          <div class="panel-title">手术成功率分布</div>
          <div ref="successChartRef" class="chart-box"></div>
        </article>

        <article class="dashboard-panel">
          <div class="panel-title">核心技术参数</div>
          <div class="tech-panel">
            <div class="tech-highlight">
              <div>
                <div class="tech-highlight__label">设备精度</div>
                <div class="tech-highlight__value">0.3mm</div>
              </div>
              <div class="tech-highlight__year">2026 核心指标</div>
            </div>

            <div class="tech-grid">
              <div v-for="item in techCards" :key="item.title" class="tech-card">
                <div class="tech-card__icon">{{ item.icon }}</div>
                <div class="tech-card__body">
                  <div class="tech-card__title">{{ item.title }}</div>
                  <div class="tech-card__value">{{ item.value }}</div>
                  <div class="tech-card__note">{{ item.note }}</div>
                </div>
              </div>
            </div>
          </div>
        </article>

        <article class="dashboard-panel">
          <div class="panel-title">手术效率提升</div>
          <div ref="efficiencyChartRef" class="chart-box"></div>
        </article>

        <article class="dashboard-panel">
          <div class="panel-title">患者满意度分布</div>
          <div ref="satisfactionChartRef" class="chart-box"></div>
        </article>

        <article class="dashboard-panel dashboard-panel--table">
          <div class="panel-title">临床应用优势</div>
          <div class="table-wrap">
            <table class="advantage-table">
              <thead>
              <tr>
                <th>序号</th>
                <th>优势项目</th>
                <th>传统手术</th>
                <th>机器人辅助</th>
                <th>提升幅度</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="row in advantageRows" :key="row.id">
                <td>{{ row.id }}</td>
                <td>{{ row.label }}</td>
                <td>{{ row.traditional }}</td>
                <td>{{ row.robot }}</td>
                <td class="accent-text">{{ row.improve }}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </article>

        <article class="dashboard-panel">
          <div class="panel-title">AI 系统性能</div>
          <div ref="aiChartRef" class="chart-box"></div>
        </article>
      </div>
    </div>
  </section>
</template>

<style scoped>
.dashboard-section {
  position: relative;
  min-height: 100vh;
  padding: 88px 16px 44px;
  background:
      linear-gradient(180deg, rgba(2, 8, 26, 0.84), rgba(2, 8, 26, 0.96)),
      url('/dashboard-robot/beijing.png') center / cover no-repeat;
  color: #d9ffff;
  opacity: 0;
  transform: translateY(42px);
  transition: opacity 0.7s ease, transform 0.7s ease;
}

.dashboard-section.visible {
  opacity: 1;
  transform: translateY(0);
}

.dashboard-section::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
      radial-gradient(circle at 18% 20%, rgba(0, 255, 255, 0.16), transparent 28%),
      radial-gradient(circle at 82% 22%, rgba(0, 140, 255, 0.22), transparent 32%),
      radial-gradient(circle at 50% 80%, rgba(0, 255, 255, 0.08), transparent 38%);
  pointer-events: none;
}

.dashboard-shell {
  position: relative;
  z-index: 1;
  max-width: 1740px;
  margin: 0 auto;
}

.dashboard-header {
  display: grid;
  gap: 6px;
  justify-items: center;
  margin-bottom: 12px;
  text-align: center;
}

.dashboard-kicker {
  margin: 0;
  letter-spacing: 0.38em;
  font-size: 11px;
  color: rgba(134, 247, 255, 0.85);
}

.dashboard-title {
  margin: 0;
  font-size: clamp(24px, 2.6vw, 34px);
  font-weight: 700;
  color: #f2ffff;
  text-shadow: 0 0 18px rgba(0, 255, 255, 0.45);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(300px, 0.74fr) minmax(700px, 1.52fr) minmax(300px, 0.74fr);
  grid-template-rows: 1fr 1fr 1fr;
  gap: 10px;
  min-height: calc(100vh - 160px);
}

.dashboard-panel {
  position: relative;
  overflow: hidden;
  padding: 10px;
  border: 1px solid rgba(0, 255, 255, 0.24);
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(2, 23, 44, 0.76), rgba(2, 11, 27, 0.88));
  box-shadow:
      inset 0 0 0 1px rgba(255, 255, 255, 0.02),
      0 0 30px rgba(0, 255, 255, 0.08);
  backdrop-filter: blur(10px);
}

.dashboard-panel::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05), transparent 28%);
  pointer-events: none;
}

.dashboard-panel--video {
  grid-row: 1 / 3;
  grid-column: 2 / 3;
}

.dashboard-panel--table {
  overflow: auto;
}

.panel-title {
  position: relative;
  z-index: 1;
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 700;
  color: #00ffff;
  text-shadow: 0 0 12px rgba(0, 255, 255, 0.35);
}

.chart-box {
  position: relative;
  z-index: 1;
  height: calc(100% - 24px);
  min-height: 165px;
}

.video-frame {
  position: relative;
  z-index: 1;
  height: calc(100% - 24px);
  min-height: 250px;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(0, 9, 22, 0.65);
  border: 1px solid rgba(0, 255, 255, 0.18);
}

.video-frame::after {
  content: '';
  position: absolute;
  inset: auto 0 0 0;
  height: 40%;
  background: linear-gradient(180deg, transparent, rgba(0, 8, 20, 0.5));
  pointer-events: none;
}

.device-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.tech-panel {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 8px;
  height: calc(100% - 24px);
}

.tech-highlight {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px;
  border-radius: 12px;
  background: rgba(0, 255, 255, 0.08);
  border: 1px solid rgba(0, 255, 255, 0.18);
}

.tech-highlight__label {
  font-size: 13px;
  color: #86f7ff;
}

.tech-highlight__value {
  margin-top: 4px;
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
}

.tech-highlight__year {
  font-size: 12px;
  color: rgba(134, 247, 255, 0.8);
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px;
}

.tech-card {
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 8px;
  border-radius: 10px;
  background: rgba(5, 43, 70, 0.68);
  border: 1px solid rgba(0, 255, 255, 0.16);
}

.tech-card__icon {
  flex: 0 0 34px;
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: rgba(0, 255, 255, 0.14);
  color: #00ffff;
  font-size: 10px;
  font-weight: 700;
}

.tech-card__body {
  min-width: 0;
}

.tech-card__title {
  font-size: 11px;
  color: rgba(222, 255, 255, 0.74);
}

.tech-card__value {
  margin-top: 2px;
  font-size: 14px;
  font-weight: 700;
  color: #ffffff;
}

.tech-card__note {
  margin-top: 4px;
  font-size: 10px;
  color: #49ffcc;
}

.table-wrap {
  position: relative;
  z-index: 1;
  max-height: calc(100% - 24px);
  overflow: auto;
}

.advantage-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.advantage-table th,
.advantage-table td {
  padding: 6px 7px;
  text-align: left;
  border-bottom: 1px solid rgba(0, 255, 255, 0.12);
}

.advantage-table th {
  position: sticky;
  top: 0;
  background: rgba(0, 255, 255, 0.12);
  color: #c5ffff;
}

.advantage-table tbody tr:hover {
  background: rgba(0, 255, 255, 0.05);
}

.accent-text {
  color: #49ffcc;
  font-weight: 700;
}

@media (max-width: 1280px) {
  .dashboard-grid {
    grid-template-columns: 1.08fr 0.92fr;
    grid-template-rows: auto;
    min-height: auto;
    gap: 10px;
  }

  .dashboard-panel--video {
    grid-row: auto;
    grid-column: 1 / -1;
    min-height: 420px;
  }
}

@media (max-width: 860px) {
  .dashboard-section {
    padding: 74px 10px 30px;
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-panel--video {
    grid-column: auto;
  }

  .tech-grid {
    grid-template-columns: 1fr;
  }

  .tech-highlight {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
