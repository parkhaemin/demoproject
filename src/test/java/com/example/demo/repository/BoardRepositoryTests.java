package com.example.demo.repository;

import com.example.demo.domain.Board;
import com.example.demo.domain.BoardImage;
import com.example.demo.dto.BoardListAllDTO;
import com.example.demo.dto.BoardListReplyCountDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.example.demo.domain.QBoard.board;
import static org.springframework.data.domain.PageRequest.*;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    public void testInsert() {
        IntStream.rangeClosed(1,100).forEach(i -> {
            Board board = Board.builder()
                    .title("title..." + i)
                    .content("content..." + i)
                    .writer("user" + (i % 10))
                    .build();

            Board result = boardRepository.save(board);
            log.info("BNO: " + result.getBno());
        });
    }

    //DB에 있는 번호를 이용하는시는걸 주의하시면 될듯합니다.
    //헉 네 감사합니다...
    //네 그럼 전 이만
    //넵



//네 제가 100번을 삭제해서 그런가요  네 맞습니다. 없었죠 그 번호가..
//헉 네네 감사합니다 꾸벅
    @Test
    public void testSelect(){
        Long bno = 101L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        log.info(board);
    }

    @Test
    public void testUpdate(){

        Long bno = 101L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();
        board.change("update..title 100", "update content 100");

        boardRepository.save(board);
    }

    @Test
    public void testDelete(){
        Long bno = 1L;

        boardRepository.deleteById(bno);
    }

    @Test
    public void testPaging(){

        Pageable pageable = of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.findAll(pageable);

        log.info("total count: " + result.getTotalElements());
        log.info("total pages: " + result.getTotalPages());
        log.info("page number: " + result.getNumber());
        log.info("page size: " + result.getSize());

        List<Board> todoList = result.getContent();

        todoList.forEach(board->log.info(board));
    }

    @Test
    public void testSearch1(){

        Pageable pageable = of(1,10, Sort.by("bno").descending());

        boardRepository.search1(pageable);
    }

    @Test
    public void testSearchAll(){

        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = of(0, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

    }

    @Test
    public void testSearchAll2(){

        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        log.info(result.getTotalPages());

        log.info(result.getSize());

        log.info(result.getNumber());

        log.info(result.hasPrevious() + ":" + result.hasNext());

        result.getContent().forEach(board ->log.info(board));

    }

    @Test
    public void testSearchReplyCount(){

        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        //total pages
        log.info(result.getTotalPages());
        //page size
        log.info(result.getSize());
        //pageNumber
        log.info(result.getNumber());
        //prev next
        log.info(result.hasPrevious() + ": " + result.hasNext());

        result.getContent().forEach(board->log.info(board));

    }

    @Test
    public void testInsertImages(){

        Board board = Board.builder()
                .title("Image Test")
                .content("첨부파일 테스트")
                .writer("tester")
                .build();

        for(int i=0;i<3;i++){

            board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg");

        }//end for

        boardRepository.save(board);
    }

    @Test
    @Transactional
    public void testReadWithImages(){

        //반드시 존재하는 bno로 확인
        Optional<Board> result = boardRepository.findByIdWithImages(2L);

        Board board = result.orElseThrow();

        log.info(board);
        log.info("---------------------");
        //log.info(board.getImageSet());
        for(BoardImage boardImage : board.getImageSet()){
            log.info(boardImage);
        }

    }

    @Transactional
    @Commit
    @Test
    public void testModifyImages(){

        Optional<Board> result = boardRepository.findByIdWithImages(2L);

        Board board = result.orElseThrow();

        //기존의 첨부파일들은 삭제
        board.clearImages();

        //새로운 첨부파일들
        for(int i=0;i<2;i++){

            board.addImage(UUID.randomUUID().toString(), "updatefile" + i + ".jpg");
        }

        boardRepository.save(board);

    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll(){

        Long bno = 3L;

        replyRepository.deleteByBoard_Bno(bno);

        boardRepository.deleteById(bno);

    }

    @Test
    public void testInsertAll(){

        for(int i=1;i<=100;i++){

            Board board = Board.builder()
                    .title("Title.." + i)
                    .content("Content.." + i)
                    .writer("wirter.." + i)
                    .build();

            for(int j=0;j<3;j++){
                if(i % 5 == 0){
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(), i+"file"+j+".jpg");
            }
            boardRepository.save(board);

        }//end for
    }

    @Transactional
    @Test
    public void testSearchImageReplyCount(){

        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        //boardRepository.searchWithAll(null, null, pageable);

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(null, null, pageable);

        log.info("--------------------------------");

        log.info(result.getTotalElements());

        result.getContent().forEach(boardListAllDTO -> log.info(boardListAllDTO));
    }
}
