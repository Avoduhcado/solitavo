package com.avogine.solitavo.scene.util;

import java.util.Comparator;

import com.avogine.solitavo.scene.cards.Card;

/**
 *
 */
public class CardComparator implements Comparator<Card> {
	@Override
	public int compare(Card o1, Card o2) {
		if (o1.isSelected() && !o2.isSelected()) {
			return 1;
		} else if (!o1.isSelected() && o2.isSelected()) {
			return -1;
		} else {
			if (o1.getPosition().y == o2.getPosition().y) {
				if (o1.getPosition().x == o2.getPosition().x) {
					return o1.getRank().compareTo(o2.getRank());
				}
				return (int) (o1.getPosition().x - o2.getPosition().x);
			}
			return (int) (o1.getPosition().y - o2.getPosition().y);
		}
	}
}
