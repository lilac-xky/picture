import { useLoginUserStore } from '@/stores/useLoginUserStore';

export default class PictureEditWebSocket {
    private pictureId: number
    private socket: WebSocket | null
    private eventHandlers: any

    constructor(pictureId: number) {
        this.pictureId = pictureId
        this.socket = null
        this.eventHandlers = {}
    }

    // 连接 WebSocket
    connect() {
        const userStore = useLoginUserStore();
        const token = localStorage.getItem('token') || (userStore.loginUser as any)?.token || '';
        if (!token) {
            console.error("无 Token，无法建立 WebSocket 连接");
            return;
        }
        const url = `ws://localhost:9090/api/ws/picture/edit?pictureId=${this.pictureId}&token=${token}`
        this.socket = new WebSocket(url)
        // 设置二进制数据类型为 Blob
        this.socket.binaryType = 'blob'
        // 监听 WebSocket 事件
        this.socket.onopen = () => {
            this.triggerEvent('open')
        }
        // 监听消息事件
        this.socket.onmessage = (event) => {
            const message = JSON.parse(event.data)
            const type = message.type
            this.triggerEvent(type, message)
        }
        // 监听关闭事件
        this.socket.onclose = (event) => {
            this.triggerEvent('close', event)
        }
        // 监听错误事件
        this.socket.onerror = (error) => {
            this.triggerEvent('error', error)
        }
    }

    // 断开 WebSocket
    disconnect() {
        if (this.socket) {
            this.socket.close()
        }
    }

    // 发送消息
    sendMessage(message: object) {
        if (this.socket && this.socket.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify(message))
        } else {
            console.error('WebSocket 未连接，无法发送消息:', message)
        }
    }

    // 添加事件监听器
    on(type: string, handler: (data?: any) => void) {
        if (!this.eventHandlers[type]) {
            this.eventHandlers[type] = []
        }
        this.eventHandlers[type].push(handler)
    }

    // 触发事件
    triggerEvent(type: string, data?: any) {
        const handlers = this.eventHandlers[type]
        if (handlers) {
            handlers.forEach((handler: any) => handler(data))
        }
    }
}