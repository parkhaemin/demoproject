package com.example.demo.domain;


import jakarta.persistence.*;
import lombok.*;

//모니터 가 듀얼?
//아니오
//속도가 엄청 느리네요
//카페?아니오 집입니다
//알겠습니다. 이상하게 속도가 안나오네요.. ㄴ

@Entity
@Table(name = "Reply", indexes = {
        @Index(name = "idx_reply_board_bno", columnList = "board_bno")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")
public class Reply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    private String replyText;

    private String replyer;

    public void changeText(String text){
        this.replyText = text;
    }

}
