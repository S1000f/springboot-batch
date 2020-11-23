package my.learning.springbootbatch.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InacttiveStepListener {

    // 어노테이션 기반의 배치 리스너
    @BeforeStep
    public void beforeStep() {
        log.info("Before step");
    }

    @AfterStep
    public void afterStep() {
        log.info("After step");
    }
}
