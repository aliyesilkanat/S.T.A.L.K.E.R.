package com.aliyesilkanat.stalker.util;

public enum Tag {
	URL {

		@Override
		public String text() {
			return "url";
		}
	},
	ID {
		@Override
		public String text() {
			return "@id";
		}
	},
	TYPE {
		@Override
		public String text() {
			return "@type";
		}
	},
	PERSON {
		@Override
		public String text() {
			return "Person";
		}
	},
	SCHEMA {
		@Override
		public String text() {
			return "http://schema.org/";
		}
	},
	CONTEXT {
		@Override
		public String text() {
			return "@context";
		}
	},
	IMAGE {

		@Override
		public String text() {
			return "image";
		}
	},
	USER_NAME {
		@Override
		public String text() {
			return "username";
		}
	},
	NAME {
		@Override
		public String text() {
			return "name";
		}
	},
	FOLLOWS {
		@Override
		public String text() {
			return "follows";
		}
	};

	public abstract String text();
}
