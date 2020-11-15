package my.learning.springbootbatch.batch;

import org.springframework.batch.item.ItemReader;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * ItemReader 의 구현체
 * @param <T>
 */
public class QueueItemReader<T> implements ItemReader<T> {

    private final Queue<T> queue;

    // 기본 ItemReader 는 DB 에서 1번씩 select 쿼리를 날려 1개씩 객체를 가져오므로 I/O 가 많이 발생하여 비효율적이다.
    // 한번의 쿼리로 대상이 되는 엔티티들을 모두 가져온 후 큐에 담은 후, 큐에서 1개씩 ItemProcessor 에게 전달한다.
    public QueueItemReader(List<T> data) {
        this.queue = new LinkedList<>(data);
    }

    @Override
    public T read() {
        return queue.poll();
    }
}
