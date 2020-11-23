package my.learning.springbootbatch.batch;

import lombok.AllArgsConstructor;
import my.learning.springbootbatch.domain.User;
import my.learning.springbootbatch.domain.enums.UserStatus;
import my.learning.springbootbatch.repository.UserRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// Tasklet 은 '읽기 -> 조작 -> 쓰기' 과정을 정의하는 Step 단위 없이, 단 하나의 Job 안에서 모든걸 처리하는 배치이다.
// 따라서 @Step 빈이 존재하지 않는다. 그리고 배치 작업 중 한번이라도 실패하면 모든 작업내역이 롤백된다.
// 즉, tasklet 의 배치작업은 단 하나의 트랜젝션으로 간주된다.
@AllArgsConstructor
@Component
public class InactiveUserTasklet implements Tasklet {

    private final UserRepository userRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // reader
        Date nowDate = (Date) chunkContext.getStepContext()
                .getJobParameters()
                .get("nowDate");

        LocalDateTime now = LocalDateTime.ofInstant(nowDate.toInstant(), ZoneId.systemDefault());
        List<User> inactiveUsers =
                userRepository.findByUpdatedDateBeforeAndStatusEquals(now.minusYears(1L), UserStatus.ACTIVE);

        // processor
        inactiveUsers = inactiveUsers.stream()
                .map(user -> user.setInactive())
                .collect(Collectors.toList());

        // writer
        userRepository.saveAll(inactiveUsers);

        return RepeatStatus.FINISHED;
    }
}
