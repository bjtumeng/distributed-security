package com.dudu.security.distributed.uaa.service;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * @Description:
 * @author:zhaomeng
 * @date:2021/10/17 10:49 下午
 */
public class TestBCrypt {
    public static void main(String[] args) {
        TestBCrypt testBCrypt = new TestBCrypt();
        testBCrypt.testBCrypt();
    }

    public void testBCrypt(){

        //对密码进行加密
        String hashpw = BCrypt.hashpw("456", BCrypt.gensalt());
        System.out.println(hashpw);

        //校验密码
        boolean checkpw = BCrypt.checkpw("123", "$2a$10$aFsOFzujtPCnUCUKcozsHux0rQ/3faAHGFSVb9Y.B1ntpmEhjRtru");
        boolean checkpw2 = BCrypt.checkpw("123", "$2a$10$HuClcUqr/FSLmzSsp9SHqe7D51Keu1sAL7tUAAcb..FyILiLdFKYy");
        System.out.println(checkpw);
        System.out.println(checkpw2);
    }
}
