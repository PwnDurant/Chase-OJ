package com.zqq.friend.domain.exam.vo;

import lombok.Getter;
import lombok.Setter;

//在排名列表中返回的数据
@Getter
@Setter
public class ExamRankVO {

    private Long userId;

    private String nickName;

    private int examRank;

    private int score;

}
