declare interface Sendable {
  send: <R = any>(command: any) => Promise<R>
}