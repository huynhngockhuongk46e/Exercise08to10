package designbook.solutions5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import designbook.chapter5.Card;
import designbook.chapter5.Rank;
import designbook.chapter5.Suit;


public class Exercise08to10 {


	private static boolean invokeIsStraightFlush(PokerHand pHand) {
		try {
			Method method = PokerHand.class.getDeclaredMethod("isStraightFlush");
			method.setAccessible(true); // cho phép truy cập method private
			return (boolean) method.invoke(pHand);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			fail();
			return false;
		}
	}

	// ------------------------------------------------------------------
	// BÀI 8: test case cho kết quả TRUE
	// 2-3-4-5-6 cùng chất CLUBS (Sảnh đồng chất)
	// ------------------------------------------------------------------
	@Test
	void testIsStraightFlush_True() {
		PokerHand hand = new PokerHand(
				Card.get(Rank.TWO, Suit.CLUBS),
				Card.get(Rank.THREE, Suit.CLUBS),
				Card.get(Rank.FOUR, Suit.CLUBS),
				Card.get(Rank.FIVE, Suit.CLUBS),
				Card.get(Rank.SIX, Suit.CLUBS));
		assertTrue(invokeIsStraightFlush(hand));
	}

	// ------------------------------------------------------------------
	// BÀI 8 (mở rộng): chứng minh con số 36
	// 9 sảnh / chất (khởi đầu từ TWO ... TEN) x 4 chất = 36 trường hợp TRUE.
	// ------------------------------------------------------------------
	@Test
	void testIsStraightFlush_AllThirtySixTrueCases() {
		Rank[] ranks = Rank.values(); // TWO ... ACE (13 phần tử)
		int count = 0;
		for (Suit suit : Suit.values()) {
			for (int start = 0; start + 4 < ranks.length; start++) {
				PokerHand hand = new PokerHand(
						Card.get(ranks[start], suit),
						Card.get(ranks[start + 1], suit),
						Card.get(ranks[start + 2], suit),
						Card.get(ranks[start + 3], suit),
						Card.get(ranks[start + 4], suit));
				assertTrue(invokeIsStraightFlush(hand));
				count++;
			}
		}
		assertEquals(36, count);
	}

	// ------------------------------------------------------------------
	// BÀI 9 & 10: test case cho kết quả FALSE
	// ------------------------------------------------------------------

	// Sai chất: lá cuối là DIAMONDS -> điều kiện "card.getSuit() != suit" đúng
	@Test
	void testIsStraightFlush_WrongSuit() {
		PokerHand hand = new PokerHand(
				Card.get(Rank.TWO, Suit.CLUBS),
				Card.get(Rank.THREE, Suit.CLUBS),
				Card.get(Rank.FOUR, Suit.CLUBS),
				Card.get(Rank.FIVE, Suit.CLUBS),
				Card.get(Rank.SIX, Suit.DIAMONDS));
		assertFalse(invokeIsStraightFlush(hand));
	}

	// Sai thứ hạng: 2-3-4-5-7 (nhảy cóc) -> "ordinal() - ordinal() != 1" đúng
	@Test
	void testIsStraightFlush_WrongRank() {
		PokerHand hand = new PokerHand(
				Card.get(Rank.TWO, Suit.CLUBS),
				Card.get(Rank.THREE, Suit.CLUBS),
				Card.get(Rank.FOUR, Suit.CLUBS),
				Card.get(Rank.FIVE, Suit.CLUBS),
				Card.get(Rank.SEVEN, Suit.CLUBS));
		assertFalse(invokeIsStraightFlush(hand));
	}
}

/**
 * Lớp đang được kiểm thử (UUT - Unit Under Test).
 */
class PokerHand {

	private final List<Card> aCards;

	public PokerHand(Card... pCards) {
		assert pCards.length == 5;
		aCards = Arrays.asList(pCards);
		aCards.sort(new Comparator<Card>() {
			public int compare(Card pCard1, Card pCard2) {
				return pCard1.getRank().compareTo(pCard2.getRank());
			}
		});
	}

	private boolean isStraightFlush() {
		Suit suit = aCards.get(0).getSuit();
		Rank rank = aCards.get(0).getRank();
		Iterator<Card> iterator = aCards.iterator();
		iterator.next();
		while (iterator.hasNext()) {
			Card card = iterator.next();
			if (card.getSuit() != suit || card.getRank().ordinal() - rank.ordinal() != 1) {
				return false;
			}
			rank = card.getRank();
		}
		return true;
	}
}
