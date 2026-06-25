package com.project.settlement_batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SettlementBatchApplication {

	/**
	 * 배치 애플리케이션의 진입점.
	 *
	 * <p>스프링 배치가 실행된 결과(성공/실패)를 운영체제나 외부 스케줄러에게 정확하게 알려주기 위해서 작성하는 표준 패턴.
	 *
	 * 웹 애플리케이션은 서버가 계속 켜져있어야 하지만, 배치는 작업을 마치고 꺼지는 것이 목표이기 때문에 어떻게 꺼지는지가 중요하다.
	 *
	 * <p>걀과적으로, 0이면 정상 종료, 1이면 비정상 종료임을 알 수 있다.
	 */
	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(SettlementBatchApplication.class, args)));
	}

}
