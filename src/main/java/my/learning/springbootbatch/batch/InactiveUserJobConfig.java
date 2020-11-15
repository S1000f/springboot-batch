package my.learning.springbootbatch.batch;

import lombok.AllArgsConstructor;
import my.learning.springbootbatch.domain.User;
import my.learning.springbootbatch.domain.enums.UserStatus;
import my.learning.springbootbatch.repository.UserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Configuration
public class InactiveUserJobConfig {

    private final static int CHUNK_SIZE = 15;
    private final EntityManagerFactory entityManagerFactory;
    private final UserRepository userRepository;

    // Job 설정
    @Bean
    public Job inactiveUserJob(JobBuilderFactory jobBuilderFactory, Step inactiveJobStep) {
        return jobBuilderFactory.get("inactiveUserJob")
                .preventRestart()
                .start(inactiveJobStep)
                .build();
    }

    // Step 설정
    @Bean
    public Step inactiveJobStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("inactiveUserStep")
                // 입력타입과 쓰기타입이 모두 User 이다. 청크 사이즈는 한 커밋당 몇 개의 쓰기를 할지 설정한다
                .<User, User> chunk(10)
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

    // ItemReader 구현체 직접구현
    @Bean
    // 스프링의 기본 빈 생성전략은 싱글턴이지만, @StepScope 가 붙은 객체는 매 실행마다 새로운 빈을 생성한다.
    // @StepScope 사용시 반환하는 타입은 반드시 사용자가 직접 오버라이딩한 구현체여야 한다(즉, 인터페이스가 아니라
    // 클래스를 반환해야 한다)
    @StepScope
    public QueueItemReader<User> inactiveUserReader() {
        List<User> oldUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(
                LocalDateTime.now().minusYears(1L), UserStatus.ACTIVE
        );
        return new QueueItemReader<>(oldUsers);
    }

    // 대상이 되는 모든 엔티티를 조회해서 메모리에 저장 후 청크 단위로 넘겨주는 스프링 기본 구현체 중 하나
    // 대상 데이터가 수만, 수십만이 되면 이러한 방식은 불가능함.
    @Bean
    @StepScope
    public ListItemReader<User> inactiveUserReader2() {
        List<User> targetUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(
                LocalDateTime.now().minusYears(1L), UserStatus.ACTIVE
        );
        return new ListItemReader<>(targetUsers);
    }

    // destroyMethod = "" 는 없는 값이므로(잘못된 설정값) 해당 기능을 사용하지 않겠다는 의미이다.
    // 배치작업이 진행되는 동안 이 빈의 인스턴스가 삭제되는 것을 방지한다.
    @Bean(destroyMethod = "")
    @StepScope
    public JpaPagingItemReader<User> inactiveUserReader3() {
        JpaPagingItemReader<User> jpaPagingItemReader = new JpaPagingItemReader<User>() {
            @Override
            public int getPage() {
                return 0;
            }
        };
        jpaPagingItemReader.setQueryString("select u from User u where u.updatedDate <:updatedDate and u.status=:status");

        Map<String, Object> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        map.put("updatedDate", now.minusYears(1L));
        map.put("status", UserStatus.ACTIVE);

        jpaPagingItemReader.setParameterValues(map);
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
        jpaPagingItemReader.setPageSize(CHUNK_SIZE);

        return jpaPagingItemReader;
    }

    // ItemProcessor 직접 구현
    public ItemProcessor<User, User> inactiveUserProcessor() {
        return user -> user.setInactive();
    }

    // JpaItemWriter 구현체 사용
    public JpaItemWriter<User> inactiveUserWriter2() {
        JpaItemWriter<User> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);

        return jpaItemWriter;
    }

    // ItemWriter 구현체
    public ItemWriter<User> inactiveUserWriter() {
        return (List<? extends User> users) -> userRepository.saveAll(users);
    }


}
