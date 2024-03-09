# 스트림에는 부작용 없는 함수를 사용하라.

스트림은 단순한 또 하나의 API가 아닌 함수형 프로그래밍에 기초한 패러다임이다. 스트림이 제공하는 표현력, 속도, 상황에 따른 병렬성 등을 얻기 위해서는 API는 말할 것도 없고, 이 패러다임도 함께 받아들여야 한다.

스트림 패러다임의 핵심은 계산을 일련의 변환(transformation)으로 재구성하는 부분이다. 이 때 각 변환 단계는 가능한 한 이전 단계의 결과를 받아 처리하는 순수 함수여야 한다.

> 순수 함수란, 오직 입력만이 결과에 영향을 주는 함수를 말하며, 다른 가변 상태를 참조하지 않고, 함수 스스로도 다른 상태를 변경하지 않는 것

이를 지키기 위해서는 중간 및 종단에서 스트림 연산에 건네는 함수 객체는 모두 부작용(side effect)가 없어야 한다.

```java
class Member {  
	String name;  
}  
  
static class MemberDto {  
	String name;  
	MemberDto(String name) {  
		this.name = name;  
	}  
	  
	static MemberDto from(Member member) {  
		return new MemberDto(member.name);  
	}  
}  

public static void main(String[] args) {  
	final List<MemberDto> dtoList = new ArrayList<>();  
	findAllMember().stream().forEach( member ->  
		dtoList.add(MemberDto.from(member))  
	);  
}
```

위 코드는 모든 회원을 가져온 뒤, 해당 `Entity`를 `Dto`로 변환하는 코드이다. 정상적으로 잘 동작하지만 위 코드는 스트림이라 할 수 없는 일반적인 반복 코드이다.

```java
final List<MemberDto> dtoList = findAllMember().stream()  
	.map(MemberDto::from)  
	.toList();
```

그에 비해 위 코드는 동일한 일을 하지만 스트림 API를 제대로 활용했다. 이렇듯 `forEach` 연산은 종단 연산 중 기능이 가장 적고, 가장 **'덜'** 스트림스럽다. 스트림 계산 결과를 보고할 때만 사용하고, 계산하는 데에는 사용하지 않는 것이 좋다.

위 코드를 토대로 각 회원이 댓글을 몇 개 달았는지를 구해보자.

```java
Map<MemberDto, Long> collect = findAllMemberByPostId(1L).stream()  
	.collect(  
		groupingBy(MemberDto::from, counting())  
	);  
System.out.println(collect);
```

```
{
	MemberDto[name=Joyce]=2, 
	MemberDto[name=Pie]=1, 
	MemberDto[name=Donald]=1
}
```

위와 같이 `collect`를 사용하면 스트림 원소를 손쉽게 컬렉션으로 모을 수 있다. 이러한 수집기는 그저 축소(reduction) 전략을 캡슐화한 블랙박스 객체이다. 여기서 축소는 스트림의 원소들을 객체 하나에 취합한다는 의미이다. 이렇듯 수집기를 이용하면 스트림의 원소를 손쉽게 컬렉션으로 모을 수 있다.

## 정리

- 스트림 파이프 라인 프로그래밍의 핵심은 부작용 없는 함수 객체에 있다.
- 스트림뿐 아니라 스트림 관련 객체에 건네지는 모든 함수 객체가 부작용이 없어야 한다.
- 종단 연산 중 `forEach`는 스트림이 수행한 계산 결과를 보고할 때만 이용하자.
    - 계산 자체에는 이용하지 말자.