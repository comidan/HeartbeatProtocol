package protocol.server.protocols.heartbeat;

public interface Observable<T, D> {

    void notify(T t, D d);
}
