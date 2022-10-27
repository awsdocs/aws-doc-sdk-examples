use fake::{
    faker::{lorem::en::Sentence, name::en::Name},
    Fake,
};
use rand::seq::SliceRandom;

mod happy;
mod mocked;

pub fn fake_name() -> String {
    Name().fake()
}

const GUIDES: [&str; 8] = [
    "c++",
    "dotnet",
    "golang",
    "java",
    "javascript",
    "php",
    "ruby",
    "rust",
];

pub fn fake_guide() -> String {
    GUIDES
        .choose(&mut rand::thread_rng())
        .unwrap_or(&"unknown")
        .to_string()
}
pub fn fake_description() -> String {
    Sentence(1..2).fake()
}
