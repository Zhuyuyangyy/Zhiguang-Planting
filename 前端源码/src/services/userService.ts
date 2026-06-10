import apiService from './apiService';
//AI辅助生成：豆包,2026-03-28 用户服务实现
/**
 * @author 刘宏伟
 * @version 1.0
 * @date 2026/3/25
 * */
// 登录请求参数
interface LoginRequest {
  username: string;
  password: string;
}

// 注册请求参数
interface RegisterRequest {
  username: string;
  password: string;
}

// 登录响应
interface LoginResponse {
  token: string;
  id: number;
  username: string;
  role: number;
}

// 用户信息
interface UserInfo {
  id: number;
  username: string;
  role: number;
  status: number;
}

// 分页用户列表
interface UserPageResponse {
  list: UserInfo[];
  total: number;
  pageNum: number;
  pageSize: number;
  totalPages: number;
}

class UserService {
  // 用户登录
  async login(data: LoginRequest): Promise<LoginResponse> {
    return apiService.post<LoginResponse>('/api/user/login', data);
  }

  // 用户注册
  async register(data: RegisterRequest): Promise<void> {
    return apiService.post<void>('/api/user/register', data);
  }

  // 获取用户信息
  async getUserInfo(): Promise<UserInfo> {
    return apiService.get<UserInfo>('/api/user/info');
  }

  // 分页查询用户列表（管理员）
  async getUserList(pageNum: number = 1, pageSize: number = 10): Promise<UserPageResponse> {
    return apiService.get<UserPageResponse>('/api/admin/user/list', {
      pageNum,
      pageSize
    });
  }

  // 更新用户状态（管理员）
  async updateUserStatus(userId: number, status: number): Promise<string> {
    return apiService.get<string>('/api/admin/user/status', {
      userId,
      status
    });
  }

  // 保存token到本地存储
  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  // 从本地存储获取token
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // 清除token
  clearToken(): void {
    localStorage.removeItem('token');
  }

  // 检查是否已登录
  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}

export default new UserService();
