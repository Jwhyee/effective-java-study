package ka.chapter7.item46;

import org.checkerframework.checker.units.qual.A;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class SideEffectTest {

    static class Member {
        Member(String name) {
            this.name = name;
        }
        String name;
    }

    static record MemberDto(String name) {

        static MemberDto from(Member member) {
            return new MemberDto(member.name);
        }
    }


    public static List<Member> findAllMember() {
        return List.of(new Member("Joyce"), new Member("Donald"), new Member("Joyce"), new Member("Pie"));
    }

    public static void main(String[] args) {
//        final List<MemberDto> dtoList = new ArrayList<>();
//        findAllMember().stream().forEach( member ->
//            dtoList.add(MemberDto.from(member))
//        );
        final List<MemberDto> dtoList = findAllMember().stream()
                .map(MemberDto::from)
                .toList();

        Map<MemberDto, Long> collect = findAllMember().stream()
                .collect(
                        groupingBy(MemberDto::from, counting())
                );

        System.out.println(collect);

        System.out.println(dtoList);
    }

    public static void findSameWord() {
//        Map<String, Long> freq = new HashMap<>();
//        try (Stream<String> words = new Scanner(file).tokens()) {
//            words.forEach(word -> {
//                freq.merge(word.toLowerCase(), 1L, Long::sum);
//            });
//        }
//
//        Map<String, Long> freq = new HashMap<>();
//        try (Stream<String> words = new Scanner(file).tokens()) {
//            freq = words.collect(
//                    groupingBy(String::toLowerCase, counting())
//            );
//        }
    }
}
