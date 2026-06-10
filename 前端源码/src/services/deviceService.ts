//AI辅助生成：豆包,2026-04-04 设备服务实现
import apiService from './apiService';
/**
 * @author 刘宏伟
 * @version 1.0
 * @date 2026/3/22
 * */
// 激光设备参数
interface LaserParams {
  pulseFrequency: string;
  waterFlow: string;
  positioningAccuracy: string;
  laserPower: string;
  status: number;
}

// 设备合规信息
type ComplianceInfo = Record<string, any>;

class DeviceService {
  // 获取激光设备参数
  async getLaserParams(): Promise<LaserParams> {
    return apiService.get<LaserParams>('/api/v1/device/laser-params');
  }

  // 获取设备合规信息
  async getComplianceInfo(): Promise<ComplianceInfo> {
    return apiService.get<ComplianceInfo>('/api/v1/device/compliance');
  }

  // 设备接口测试
  async testDevice(): Promise<string> {
    return apiService.get<string>('/api/v1/device/test');
  }
}

export default new DeviceService();
