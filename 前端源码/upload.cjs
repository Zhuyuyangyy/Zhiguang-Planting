const ssh2 = require('ssh2-sftp-client');
const path = require('path');
const fs = require('fs');

const sftp = new ssh2();

const remotePath = '/var/www/html/';
const localDistPath = path.join(__dirname, 'dist');

async function deploy() {
  try {
    console.log('正在连接服务器...');
    await sftp.connect({
      host: process.env.DEPLOY_HOST,
      port: Number(process.env.DEPLOY_PORT || 22),
      username: process.env.DEPLOY_USERNAME,
      password: process.env.DEPLOY_PASSWORD
    });
    
    console.log('已连接，正在上传文件...');
    
    // 获取本地 dist 文件夹中的所有文件
    const files = fs.readdirSync(localDistPath);
    
    for (const file of files) {
      const localFile = path.join(localDistPath, file);
      const remoteFile = path.posix.join(remotePath, file);
      
      if (fs.statSync(localFile).isDirectory()) {
        // 如果是目录，先创建远程目录
        try {
          await sftp.mkdir(remoteFile, true);
        } catch (e) {}
        // 递归上传目录内容
        await uploadDir(localFile, remoteFile);
      } else {
        // 上传文件
        console.log(`上传: ${file}`);
        await sftp.put(localFile, remoteFile);
      }
    }
    
    console.log('部署完成！');
  } catch (err) {
    console.error('部署失败:', err);
  } finally {
    sftp.end();
  }
}

async function uploadDir(localDir, remoteDir) {
  const files = fs.readdirSync(localDir);
  for (const file of files) {
    const localPath = path.join(localDir, file);
    const remotePathStr = path.posix.join(remoteDir, file);
    
    if (fs.statSync(localPath).isDirectory()) {
      try {
        await sftp.mkdir(remotePathStr, true);
      } catch (e) {}
      await uploadDir(localPath, remotePathStr);
    } else {
      console.log(`上传: ${file}`);
      await sftp.put(localPath, remotePathStr);
    }
  }
}

deploy();
