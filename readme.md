README

Design Challenge
Ilan Gray

Feel & Motion: How can animation and micro-interactions make the player feel more responsive and alive?

Animations: 
- Graceful/gradual transition from unfavorited to favorited
- Animate the bars of the pause button into the triangle of the play button (instead of just fading one in while the other fades out)

Accessibility: What are the most important accessibility considerations for a media player like this? How would you make it usable for everyone?

- Everything needs accurate contentDescriptions
- The order of the views as read by Talkback must make sense. The user should be able to easily navigate through the UI with talkback on.
- Make sure colors have good constrast (the arbitrary ones i selected most likely do not)
- Make sure the sizing of the widget respects the user's OS-level font/display size setting.

Edge Cases: What are some potential edge cases in the design (e.g., long text, missing images), and how might you solve them gracefully?

Additional:
- Image fails to load (use a placeholder)
- Text too long: ellipse, or use android's autosizing to decrease the font size to make it fit, have long text scroll (like spotify does with long song names)
- Buffering: playback reaches the end of what we have buffered, so we need a sort of loading state to indicate we are fetching more. Similarly, we also need rendering of the amount fetched.

Your Vision: If you had another hour, what's one simple feature or polish element you would add to make this player even better?

- Implement the corresponding Android notification that can also control playback. 
- Implement a minimized in-app playback control that we can display if/when the user navigates to a different part of the app while continuing to list to audio. 
- List to more beyonce!!!

Cross-platform: How would you build this feature to feel native and high-quality across mobile, tablets, and the web?

- In my experience, it heavily depends on the staffing/resourcing levels, and where we want to place strategic investments. In this case, i expect we want native implementations on all platforms, but that may not always be the case, e.g. for a feature that *must* be consistent across platforms like a consent moment.